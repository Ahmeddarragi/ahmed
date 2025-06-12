package org.sid.config;

import org.sid.entity.Admin;
import org.sid.entity.Userr;
import org.sid.repository.AdminRepository;
import org.sid.repository.UserrRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

@Configuration
public class UserDetailsServiceConfig {

    private final AdminRepository adminRepository;
    private final UserrRepository userRepository;

    public UserDetailsServiceConfig(AdminRepository adminRepository, UserrRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Recherche d'un admin par nom d'utilisateur ou email
            Optional<Admin> adminByUsername = adminRepository.findByUsername(username);
            if (adminByUsername.isPresent()) {
                Admin admin = adminByUsername.get();
                return new User(
                    admin.getUsername(),
                    admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
            }

            Optional<Admin> adminByEmail = adminRepository.findByEmail(username);
            if (adminByEmail.isPresent()) {
                Admin admin = adminByEmail.get();
                return new User(
                    admin.getEmail(),
                    admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
            }

            // Utilisation d'une méthode intermédiaire pour convertir Userr en Optional<Userr>
            Optional<Userr> userByEmail = findUserByEmailOptional(username);
            if (userByEmail.isPresent()) {
                Userr user = userByEmail.get();
                return new User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }

            throw new UsernameNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur ou l'email : " + username);
        };
    }
    
    // Méthode intermédiaire pour convertir Userr en Optional<Userr>
    private Optional<Userr> findUserByEmailOptional(String email) {
        Userr user = userRepository.findByEmail(email);
        return Optional.ofNullable(user);
    }
}
