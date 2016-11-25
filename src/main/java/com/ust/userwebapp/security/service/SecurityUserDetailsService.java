package com.ust.userwebapp.security.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.ust.userwebapp.persistence.model.Principal;
import com.ust.userwebapp.services.usermanagement.IPrincipalService;

/**
 * Database user authentication service.
 */
@Component("userDetailsService")
public final class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    private IPrincipalService principalService;

    public SecurityUserDetailsService() {
        super();
    }

    // API - public

    /**
     * Loads the user from the datastore, by it's user name <br>
     */
    @Override
    public final UserDetails loadUserByUsername(final String username) {
        Preconditions.checkNotNull(username);

        final Principal principal = principalService.findByName(username);
        if (principal == null) {
            throw new UsernameNotFoundException("Username was not found: " + username);
        }

        final List<GrantedAuthority> authorities = new ArrayList<>();
        principal.getRoles().forEach(role -> {
            if (role != null) {
                authorities.addAll(role.getPrivileges().stream().map(priv -> new SimpleGrantedAuthority(priv.getName())).distinct().collect(Collectors.toList()));
            }
        });

        return new User(principal.getName(), principal.getPassword(), authorities);
    }

}
