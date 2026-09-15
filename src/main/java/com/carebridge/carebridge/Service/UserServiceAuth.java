package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.Utils.JWTutil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceAuth implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.carebridge.carebridge.entity.UserDetails user=userRepo.findByEmail(username);
        if (user != null) {

            UserDetails userdetails =
                    org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .roles(user.getRole())
                            .build();

            return userdetails;
        }

        throw new UsernameNotFoundException(
                "User not found with username: " + username
        );
    }
}