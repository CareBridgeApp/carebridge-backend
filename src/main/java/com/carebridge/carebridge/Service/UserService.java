package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.entity.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public void saveuserdetails(UserDetails userDetails) {
        try{
            userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            userRepo.save(userDetails);
        }catch(Exception e){
            log.error("Error while saving userdetails!", e);
        }
    }

}
