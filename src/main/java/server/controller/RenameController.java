package server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import server.database.model.Role;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.RoleRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.SubjectForm;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class RenameController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserLinkRepository userLinkRepository;

    private long id;

    @GetMapping(path = "/rename/*")
    public String rename(Principal principal, Model model, HttpServletRequest request){

        User subject;
        id = getIdUrl(request);
        subject = userRepository.findOne(id);
        logger.info(Long.toString(id));
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

        model.addAttribute("subject",subject);
        model.addAttribute("subjects", subjects);
        model.addAttribute("form", new SubjectForm());
        return "rename";
    }
    private long getIdUrl(HttpServletRequest request)
    {
        String idStr=request.getRequestURL().toString() + "?" + request.getQueryString();
        String[] temp= idStr.split(Pattern.quote("?"));
        temp=temp[0].split(Pattern.quote("/"));
        for(String i : temp){
            logger.info(i);
        }


        return Integer.parseInt(temp[temp.length-1]);
    }
    @PostMapping(path = "/rename")
    public String renameForm(Authentication authentication, Model model,
                                 @ModelAttribute(name = "form") SubjectForm form) {
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
                .getAuthorities();

        User user = userRepository.findByEmail(authentication.getName());
        User subject = userRepository.findOne(id);
        subject.setFirstName(form.getFirstName());
        subject.setLastName(form.getLastName());
        userRepository.save(subject);
        //userRepository.save(subject);
    return "redirect:/settings";
    }
}
