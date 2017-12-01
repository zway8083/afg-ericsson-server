package server.config.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import server.database.model.Role;
import server.database.model.User;
import server.database.repository.UserRepository;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserRepository userRepository;

	public MyUserDetailsService() {
		super();
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = null;
		try {
			user = userRepository.findByEmail(email);
		} catch (Exception e) {
			logger.error("Cannot get username '" + email + "' from DB: " + e.getMessage());
		}
		if (user == null)
			throw new UsernameNotFoundException("No user found with username: " + email);
		org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
				user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true,
				getAuthorities(user.getRoles()));
		return userDetails;
	}

	private final Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			authorities.addAll(role.getPrivileges().stream().map(p -> new SimpleGrantedAuthority(p.getName()))
					.collect(Collectors.toList()));
		}
		return authorities;
	}
}
