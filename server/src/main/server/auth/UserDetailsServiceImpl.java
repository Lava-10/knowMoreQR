package com.knowMoreQR.server.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ConsumerLoginRepository consumerRepo;

    @Autowired
    private CompanyLoginRepository companyRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<ConsumerLogin> consumerOpt = consumerRepo.findByEmail(email);
        if (consumerOpt.isPresent()) {
            ConsumerLogin consumer = consumerOpt.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_CONSUMER"));
            // You can add more specific roles/authorities if needed
            return new User(consumer.getEmail(), consumer.getPasswordHash(), authorities);
        }

        Optional<CompanyLogin> companyOpt = companyRepo.findByEmail(email);
        if (companyOpt.isPresent()) {
            CompanyLogin company = companyOpt.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_COMPANY"));
            // You can add more specific roles/authorities if needed
            return new User(company.getEmail(), company.getPasswordHash(), authorities);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
} 