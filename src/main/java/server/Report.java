package server;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.Device;
import server.database.model.User;
import server.database.repository.DeviceRepository;
import server.database.repository.EventRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.SensorTypeRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.ReportInfos;
import server.task.EventTask;

@Controller
public class Report {
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
	public String reportForm(Model model) {
		List<User> users = userRepository.findBySubject(true);
		model.addAttribute("users", users);

		List<String> actions = Arrays.asList("Télécharger les relevés", "Envoyer les relevés par email");
		model.addAttribute("actions", actions);

		ReportInfos report = new ReportInfos();
		model.addAttribute("report", report);

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
	public void reportDwnld(HttpServletResponse response, @ModelAttribute ReportInfos report) throws IOException {
		logger.info("Repport requested for user id = " + report.getId() + ", date = " + report.getDate());

		List<String> actions = report.getActions();
		if (actions.isEmpty())
			return;

		User user = userRepository.findOne(report.getId());
		Device device = deviceRepository.findOneByUser(user);

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime date = formatter.parseDateTime(report.getDate());

		EventTask eventTask = new EventTask(device, date, sensorTypeRepository, eventRepository, eventStatRepository,
				userLinkRepository, path);
		ArrayList<String> files = eventTask.createCsvReport();
		if (files == null || files.isEmpty())
			return;

		if (actions.contains("Télécharger les relevés")) {
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

		if (actions.contains("Envoyer les relevés par email")) {
			List<String> recipients = eventTask.sendEmail(id, password, host, files);
			if (recipients == null || recipients.isEmpty()) {
				response.getWriter().write("Error, nothing sent");
				return;
			}

			response.getWriter().write("Email sent to: ");
			for (String recipient : recipients)
				response.getWriter().write(recipient + " ");
		}
	}
}
