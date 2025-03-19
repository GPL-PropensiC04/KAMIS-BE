package gpl.karina.profile.security.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import gpl.karina.profile.model.EndUser;
import gpl.karina.profile.repository.EndUserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final EndUserRepository endUserRepository;

    public UserDetailsServiceImpl(EndUserRepository endUserRepository) {
        this.endUserRepository = endUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<EndUser> endUser = endUserRepository.findByUsername(username);
        if (endUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        String role = endUser.get().getClass().getSimpleName();
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return new User(endUser.get().getUsername(), endUser.get().getPassword(), authorities);
    }
}
