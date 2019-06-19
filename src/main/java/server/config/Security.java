package server.config;

import org.springframework.security.core.Authentication;
import server.database.model.Device;
import server.database.model.User;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import java.security.Principal;

public class Security {
    public UserLinkRepository getUserLinkRepository() {
        return userLinkRepository;
    }

    public void setUserLinkRepository(UserLinkRepository userLinkRepository) {
        this.userLinkRepository = userLinkRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserLinkRepository userLinkRepository;
    private UserRepository userRepository;

    public Security(UserRepository userRepository,UserLinkRepository userLinkRepository){
        this.userRepository=userRepository;
        this.userLinkRepository=userLinkRepository;
    }

    /*
    Methodes checkAutority permettant de vérifier si un élément à une authorité sur un autre élément.
    Principalement l'utilisateur (Principal, Authtication, User) sur:
    un sujet (User) ou un capteur (Device)
     */
    public boolean checkAutority(Principal principal, User subject){
        return !userLinkRepository.findBySubjectAndUser(subject, userRepository.findByEmail(principal.getName())).isEmpty();
    }
    public boolean checkAutority(User parent,User subject){
        return !userLinkRepository.findBySubjectAndUser(subject,parent).isEmpty();
    }
    public boolean checkAutority(User parent,Device device){
        return checkAutority(parent,device.getUser());
    }
    public boolean checkAutority(Principal principal, Device device){
        return checkAutority(principal,device.getUser());
    }
    public boolean checkAutority(Authentication authentication, User subject){
        return checkAutority(userRepository.findByEmail(authentication.getName()),subject);
    }
    public boolean checkAutority(Authentication authentication, Device device){
        return checkAutority(userRepository.findByEmail(authentication.getName()),device.getUser());
    }


}
