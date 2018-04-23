package server.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.Device;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.DeviceRepository;
import server.database.repository.EventRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.SensorTypeRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.exception.MissingSleepTimesException;
import server.exception.NoMotionException;
import server.model.ReportInfos;
import server.task.EventTask;
import server.utils.DateConverter;

@Controller
public class ReportController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private SensorTypeRepository sensorTypeRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventStatRepository eventStatRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;

	@Value("${report.path}")
	private String path;
	@Value("${report.email.id}")
	private String id;
	@Value("${report.email.password}")
	private String password;
	@Value("${report.email.host}")
	private String host;

	@GetMapping(path = "/report")
	public String reportForm(Authentication authentication, Model model) {
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
				.getAuthorities();

		List<User> subjects = null;
		if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			subjects = userRepository.findBySubject(true);
		} else {
			subjects = new ArrayList<>();
			User curUser = userRepository.findByEmail(authentication.getName());
			List<UserLink> links = userLinkRepository.findByUser(curUser);
			for (UserLink userLink : links)
			{
				if(!subjects.contains(userLink.getSubject()))
				subjects.add(userLink.getSubject());
			}
			
		}

		model.addAttribute("initForm", true);
		model.addAttribute("subjects", subjects);
		model.addAttribute("report", new ReportInfos());

		return "report";
	}

	private String zipFiles(String zipFileName, List<String> filePaths) {
		try {
			File firstFile = new File(filePaths.get(0));
			String zipFilePath = firstFile.getParent() + "/" + zipFileName;

			FileOutputStream fos = new FileOutputStream(zipFilePath);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String aFile : filePaths) {
				zos.putNextEntry(new ZipEntry(new File(aFile).getName()));

				byte[] bytes = Files.readAllBytes(Paths.get(aFile));
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();
			}
			zos.close();
			return zipFilePath;
		} catch (FileNotFoundException ex) {
			logger.error("Error creating zip file: file does not exist: " + ex);
			return null;
		} catch (IOException ex) {
			logger.error("Error creating zip file: I/O error: " + ex);
			return null;
		}
	}

	@PostMapping(path = "/report")
	public String report(@ModelAttribute(name = "report") ReportInfos report, Model model) {
		
		DateTime curDate = DateConverter.toDateTime(report.getDate());
		String prevDate = DateConverter.toFormatString(curDate.minusDays(1));
		model.addAttribute("prevReport", new ReportInfos(report.getId(), prevDate));
		String nextDate = DateConverter.toFormatString(curDate.plusDays(1));
		model.addAttribute("nextReport", new ReportInfos(report.getId(), nextDate));

		User subject = userRepository.findOne(report.getId());
		Device device = deviceRepository.findOneByUser(subject);
		if (device == null)
			return "redirect:/report?error=device";
		try {
			EventTask eventTask = new EventTask(device, curDate, sensorTypeRepository, eventRepository,
					eventStatRepository, userLinkRepository, path);
			String reportHTML = eventTask.createHTMLBody();
			model.addAttribute("reportHTML", reportHTML);
		} catch (MissingSleepTimesException e) {
			return "redirect:/report?error=sleepTimes";
		} catch (NoMotionException e) {
			return "redirect:/report?error=data";
		}

		model.addAttribute("showReport", true);
		model.addAttribute("report", report);

		return "report";
	}

	@PostMapping(path = "/report/mail")
	public String reportMail(Principal principal, @ModelAttribute ReportInfos report, Model model) {
		DateTime date = new DateTime(DateConverter.toSQLDate(report.getDate()).getTime());
		User subject = userRepository.findOne(report.getId());
		Device device = deviceRepository.findOneByUser(subject);

		List<String> recipients = null;
		try {
			EventTask eventTask = new EventTask(device, date, sensorTypeRepository, eventRepository,
					eventStatRepository, userLinkRepository, path);
			recipients = eventTask.sendEmail(Arrays.asList(principal.getName()), id, password, host, null);
		} catch (MissingSleepTimesException e) {
			return "redirect:/report?error=sleepTimes";
		} catch (NoMotionException e) {
			return "redirect:/report?error=data";
		}

		if (recipients == null)
			return "redirect:/report?error=email&email=" + principal.getName();
		return "redirect:/report?email=" + principal.getName();
	}

	@PostMapping(path = "/report/dwn")
	public void reportDwn(HttpServletResponse response, Principal principal, @ModelAttribute ReportInfos report)
			throws IOException {
		DateTime date = new DateTime(DateConverter.toSQLDate(report.getDate()).getTime());
		User user = userRepository.findByEmail(principal.getName());
		User subject = userRepository.findOne(report.getId());
		Device device = deviceRepository.findOneByUser(subject);

		EventTask eventTask = null;
		try {
			eventTask = new EventTask(device, date, sensorTypeRepository, eventRepository, eventStatRepository,
					userLinkRepository, path);
		} catch (MissingSleepTimesException e) {
			response.sendRedirect("/report?error=sleepTimes");
		} catch (NoMotionException e) {
			response.sendRedirect("/report?error=data");
		}

		ArrayList<String> files = eventTask.createCsvReport();

		String name = "reports_" + date.toString("dd-MM-yyyy") + "_" + user.getId() + ".zip";
		String zipFilePath = zipFiles(name, files);
		if (zipFilePath == null)
			return;
		File zipFile = new File(zipFilePath);

		String mimeType = URLConnection.guessContentTypeFromName(zipFilePath);
		if (mimeType == null)
			mimeType = "application/octet-stream";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", zipFile.getName()));
		InputStream inputStream = new BufferedInputStream(new FileInputStream(zipFile));
		FileCopyUtils.copy(inputStream, response.getOutputStream());
	}
}
