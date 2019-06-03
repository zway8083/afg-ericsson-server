package server.config;

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

    public Security(){}

    public boolean checkAutority(Principal principal, User subject){
        return !userLinkRepository.findBySubjectAndUser(subject, userRepository.findByEmail(principal.getName())).isEmpty();
    }
    public boolean checkAutority(User parent,User subject){
        return !userLinkRepository.findBySubjectAndUser(subject,parent).isEmpty();
    }


}
