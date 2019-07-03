package server.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.database.model.*;
import server.database.repository.*;


public class DeviceTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EventRepository eventRepository;
    private DeviceRepository deviceRepository;
    private RoleRepository roleRepository;
    private UserLinkRepository userLinkRepository;
    private String path;
    private DateTime date;
    private String EmailParent;
    private Date lastconnectiondate;


    private String first_name;
    private String last_name;

    public DeviceTask(DateTime date, EventRepository eventRepository,DeviceRepository deviceRepository,RoleRepository roleRepository,UserLinkRepository userLinkRepository, String path){
        this.date=date;
        this.eventRepository = eventRepository;
        this.deviceRepository = deviceRepository;
        this.path=path;
        this.roleRepository=roleRepository;
        this.userLinkRepository=userLinkRepository;
    }




    public ArrayList<String> createCsvActiveListDevice() {
        List<Event> allevents =eventRepository.findAll();
        if (allevents.isEmpty())
            return null;

        logger.info("Creating csv files for active Device");
        ArrayList<String> fileNames = new ArrayList<>();
        String fileName = path + '/' + "Liste_Capteurs_déjà_activé_au_moins_une_fois_au_" + "_" + date.toString("dd-MM-yyyy") +  ".csv";
        FileWriter fileWriter = null;

		try {
		    fileWriter = new FileWriter(fileName);
		    PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println("Id du Device, Serial Number du device, Prénom Sujet Associé, Nom Sujet Associé, Premier parent(on en donne un seul ici), date de dernière connexion");

			List<Device> devices =deviceRepository.findAll();
			for(Device device: devices){
			    List<Event> events = eventRepository.findByDevice(device);
			    if (events.size()!=0){
			        //logger.info("on ajoute le device à notre csv " + device.getId());

			        if (device.getUser()==null){  //gestion si le user a supprimé son capteur après l'avoir utilisé.
                        first_name = "capteur déjà utilisé";
                        last_name  = "mais a été supprimé";
                        EmailParent = null;
                        for (Event event:events){
                            lastconnectiondate=event.getDate();
                            logger.info(lastconnectiondate.toString() + " " + device.getId());
                        }

                    }else{
                        first_name = device.getUser().getFirstName();
                        last_name  = device.getUser().getLastName();
                        Role parent = roleRepository.findByName("ROLE_PARENT");
                        List<UserLink> subjectRelated = userLinkRepository.findBySubjectAndRole(device.getUser(),parent);
                        for (UserLink userparent : subjectRelated) {
                            EmailParent= userparent.getUser().getEmail();

                        }
                        for (Event event:events){
                            lastconnectiondate=event.getDate();
                        }

                    }
                    //logger.info(EmailParent);
                    logger.info("on retient la date : " + lastconnectiondate.toString() + "pour le device : " + device.getId());


			        printWriter.printf("%s,%s,%s,%s,%s,%s\n", device.getId(), device.toString(),first_name,last_name,EmailParent,lastconnectiondate.toString());
			    }
			}
			printWriter.close();
			fileNames.add(fileName);
		} catch (IOException e) {
		    logger.error("Unable to create file " + new File(fileName).getName() + ":" + e.getMessage());
		}
        return fileNames;
    }
}









