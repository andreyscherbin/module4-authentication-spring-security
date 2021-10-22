package com.epam.esm.web.security;

import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.epam.esm.entity.UserTable.USER_NAME;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> user = userService.find(Map.of(USER_NAME, username));
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with userName: " + username + " not found");
        }

        return null;
    }
}
