package server.controller;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import server.config.Security;
import server.database.model.*;
import server.database.repository.DeviceRepository;
import server.database.repository.RoleRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.database.repository.EventRepository;
import server.model.DeviceInfos;
import server.task.DeviceTask;

import javax.servlet.http.HttpServletResponse;

@Controller
public class DeviceController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private EventRepository eventRepository;

	@Value("${report.path}")
	private String path;


	@GetMapping(path = "/mydevices")
	public String mydevices(Authentication authentication, Model model) {
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
				.getAuthorities();
		List<User> subjects;
		if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			subjects = userRepository.findBySubject(true);
		} else {
			subjects = new ArrayList<>();
			String email = authentication.getName();
	    	User curUser = userRepository.findByEmail(email);

			Role parent = roleRepository.findByName("ROLE_PARENT");
			if (curUser.getRoles().contains(parent)) {
				List<UserLink> userLinks = userLinkRepository.findByUserAndRole(curUser, parent);
				for (UserLink userLink : userLinks)
					subjects.add(userLink.getSubject());
			}
		}

		Hashtable<Long, List<Device>> hashtable = new Hashtable<>();
		for (User subject : subjects) {
			
			List<Device> devices = new ArrayList<>();
			devices= deviceRepository.findByUser(subject);
			
			hashtable.put(subject.getId(), devices);
		}

		model.addAttribute("subjects", subjects);
		model.addAttribute("hashtable", hashtable);
		model.addAttribute("device", new Device());
		model.addAttribute("ActiveAtLeastOnceDevice",new DeviceInfos());
		return "mydevices";
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

	@PostMapping(path = "/mydevices")
	public String mydevicesForm(Authentication authentication, Model model,
			@ModelAttribute(name = "device") Device device) {
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
				.getAuthorities();
		Security security=new Security(userRepository,userLinkRepository);
		User user = userRepository.findOne(device.getUserId());
		device.setUser(user);

		if(!security.checkAutority(authentication,user)){
			return mydevices(authentication,model);
		}
		String num_ser=SnGenerate();
		device.setSerial(num_ser);
		while (deviceRepository.findBySerial(device.getSerial())!=null)
		{
			num_ser=SnGenerate();
			device.setSerial(num_ser);
			System.out.println("Dans while");
		}
		
		deviceRepository.save(device);
		//AppiotRef appiotRef = new AppiotRef(device, device.getGateway());
		//appiotRefRepository.save(appiotRef);
		logger.info("Device added: " + device.getSerialStr() + " for user: " + device.getUserId());
	
		return mydevices(authentication, model);

	}
	
	@PostMapping(path = "/mydevices/{idDevice}/delete")
	public String deleteDevice(Authentication authentication,Model model,
							   @PathVariable("idDevice") long idDevice,final RedirectAttributes redirectAttributes) {
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
		Security security=new Security(userRepository,userLinkRepository);

	    Device device=deviceRepository.findOne(idDevice);
	    if(!security.checkAutority(authentication,device)){
	    	logger.info(authentication.getName()+" tried to delete "+device.getSerialStr()+" but had not the rights.");
	    	return "redirect:/mydevices";
	    }
	    device.setUser(null);
	    deviceRepository.save(device);

	    return "redirect:/mydevices";

	}
	    
	public String SnGenerate() {
		SecureRandom random = new SecureRandom();
	    String resultStr;
	    int result = random.nextInt(900)+100;
    	resultStr =result+"";
	    for(int i=1;i<8;i++) {
	    	result = random.nextInt(900)+100;
	    	resultStr =resultStr+"-"+result;
	    }
	    return resultStr;
	}

	@PostMapping(path = "/device/dwn")
	public void reportDwn(HttpServletResponse response, Principal principal, @ModelAttribute DeviceInfos devicelist)
			throws IOException {

		DateTime date = new DateTime();
		logger.info("path : " +path);
		logger.info("on prend la date " + date.toString("dd-MM-yyyy"));
		DeviceTask deviceTask = new DeviceTask(date,eventRepository,deviceRepository,roleRepository,userLinkRepository, path);

		logger.info("je suis avant la création du csv");
		ArrayList<String> files = deviceTask.createCsvActiveListDevice();

		String name = "Liste_Capteurs_déjà_activé_au_moins_une_fois_au_" + date.toString("dd-MM-yyyy") + ".zip";
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
