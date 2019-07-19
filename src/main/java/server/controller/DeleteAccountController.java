package server.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import server.config.Security;
import server.database.model.Device;
import server.database.model.Role;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.DeviceRepository;
import server.database.repository.RoleRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.DeleteAccountForm;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DeleteAccountController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLinkRepository userLinkRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping(path="/deleteaccount")
    public String trydeleteaccount(Principal principal, Model model) {
        Role admin = roleRepository.findByName("ROLE_ADMIN");
        Role parent = roleRepository.findByName("ROLE_PARENT");
        User user = userRepository.findByEmail(principal.getName());
        List<User> subjects;


        if (user.getRoles().contains(admin)) {
            subjects = userRepository.findBySubject(true);
        } else {
            subjects = new ArrayList<>();
            List<UserLink> links = userLinkRepository.findByUserAndRole(user, parent);
            if (!links.isEmpty()) {
                for (UserLink link : links)
                    subjects.add(link.getSubject());
            }
        }

        List<Device> devices = new ArrayList<>();
        for (User subject : subjects) {
            List<Device> deviceofeach = deviceRepository.findByUser(subject);
            devices.addAll(deviceofeach);
        }


        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("Id", user.getId());
        model.addAttribute("subjects", subjects);
        model.addAttribute("devices",devices);
        model.addAttribute("form",new DeleteAccountForm());
        return "deleteaccount";
    }


    @PostMapping(path="/deleteaccount")
    public String DeleteAllInfos(Authentication authentication, @ModelAttribute(name="form") DeleteAccountForm form,HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();

        String accounttodelete = request.getParameter("accounttodelete");
        if(accounttodelete!= null){
            long userId = Long.parseLong(accounttodelete);
            logger.info("On a coché la case account ayant pour id : " + userId);
            User user=userRepository.findById(userId);

            Role accompagnateur = roleRepository.findByName("ROLE_ACCOMPAGNATEUR");
            List<UserLink> userLinkaccompagnateur =userLinkRepository.findByUserAndRole(user,accompagnateur);
            for(UserLink userssubject : userLinkaccompagnateur){
                logger.info("Id : " + userssubject.getId() +" accompagnateurId : " +userssubject.getUser().getId() + " subjectId : " + userssubject.getSubject().getId());
                userLinkRepository.delete(userLinkRepository.findBySubjectAndUser(userssubject.getSubject(), userssubject.getUser()));
            }
            Role observateur = roleRepository.findByName("ROLE_OBSERVATEUR");
            List<UserLink> userLinkobservateur =userLinkRepository.findByUserAndRole(user,observateur);
            for(UserLink userssubject : userLinkobservateur){
                logger.info("Id : " + userssubject.getId() +" observateurId : " +userssubject.getUser().getId() + " subjectId : " + userssubject.getSubject().getId());
                userLinkRepository.delete(userLinkRepository.findBySubjectAndUser(userssubject.getSubject(), userssubject.getUser()));
            }
            Role parent = roleRepository.findByName("ROLE_PARENT");
            List<UserLink> userLinkparent = userLinkRepository.findByUserAndRole(user,parent);
            for(UserLink userssubject : userLinkparent){
                logger.info("Id : " + userssubject.getId() +" userId : " +userssubject.getUser().getId() + " subjectId : " + userssubject.getSubject().getId());
                List<UserLink> subjectRelated = userLinkRepository.findBySubjectAndRole(userssubject.getSubject(),parent);
                if(subjectRelated.size()==1){
                    userLinkRepository.delete(userLinkRepository.findBySubjectAndUser(userssubject.getSubject(), userssubject.getUser()));
                    User subject= userRepository.findOne(userssubject.getSubject().getId());

                    subject.setEmailON("");
                    subject.setFirstName("subject");
                    subject.setLastName("deleted");
                    userRepository.save(subject);
                    userLinkRepository.delete(userLinkRepository.findBySubject(subject));
                    List<Device> devices = deviceRepository.findByUser(subject);
                    for (Device device:devices){
                        device.setUser(null);
                        deviceRepository.save(device);
                    }
                }
                else{
                    userLinkRepository.delete(userLinkRepository.findBySubjectAndUser(userssubject.getSubject(), userssubject.getUser()));

                }
            }
            user.setEmail(null);
            user.setLastName("Deleted");
            user.setFirstName("Parent");
            userRepository.save(user);

        }


        String[] subjecttodeleteliste = request.getParameterValues("subjecttodelete");
        if (subjecttodeleteliste!=null) {
            for(String selectedsubject : subjecttodeleteliste) {
                long subjectId = Long.parseLong(selectedsubject);
                logger.info("On a coché le sujet : " + subjectId);
                User subject= userRepository.findOne(subjectId);
                subject.setEmailON("");
                subject.setFirstName("subject");
                subject.setLastName("deleted");
                userRepository.save(subject);
                userLinkRepository.delete(userLinkRepository.findBySubject(subject));
                List<Device> devices = deviceRepository.findByUser(subject);
                for (Device device:devices){
                    device.setUser(null);
                    deviceRepository.save(device);
                }
            }
        }

        String[] devicetodeleteliste = request.getParameterValues("devicetodelete");
        if (devicetodeleteliste!=null) {
            for (String selecteddevice : devicetodeleteliste) {
                long deviceId = Long.parseLong(selecteddevice);
                logger.info("On a coché le device : " + deviceId);
                Device device = deviceRepository.findOne(deviceId);
                    device.setUser(null);
                    deviceRepository.save(device);

            }
        }



        return "redirect:/login?logout";
    }

}
