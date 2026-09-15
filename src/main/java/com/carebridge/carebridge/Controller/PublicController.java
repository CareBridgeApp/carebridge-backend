package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.PatientRepo;
import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.Service.UserService;
import com.carebridge.carebridge.Utils.JWTutil;
import com.carebridge.carebridge.Dto.LoginEntry;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.PatientDetails;
import com.carebridge.carebridge.entity.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/public")
@RestController
@Slf4j
public class PublicController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTutil jwtutil;
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private UserRepo  userRepo;
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDetails userDetails) {
        UserDetails user = userRepo.findByEmail(userDetails.getEmail());
        if (user != null) {
            PatientDetails patient = patientRepo.findByUserid(userDetails.getId());
            if (patient != null) {
                return new ResponseEntity<>(Map.of("message", "Profile already exists for this user"), HttpStatus.UNPROCESSABLE_ENTITY);
            }
            else{
                userService.saveuserdetails(userDetails);
                return new ResponseEntity<>(Map.of("message", "User Details have been updated successfully!"
                        ,"userId",userDetails.getId().toHexString()), HttpStatus.CREATED);
            }
        }
        userService.saveuserdetails(userDetails);
        return new ResponseEntity<>(
                Map.of("userId", userDetails.getId().toHexString()),
                HttpStatus.OK
        );
    }

    @PostMapping("/login1")
    public ResponseEntity<?> login_user(@RequestBody LoginEntry loginEntry) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginEntry.getEmail(), loginEntry.getPassword()));
            UserDetails userDetails=userRepo.findByEmail(loginEntry.getEmail());
            String userid=userDetails.getId().toString();
            String jwt = jwtutil.generateToken(userid);
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch(Exception e){
            log.error("Error occured while signing up",e);
            return new ResponseEntity<>(Map.of("message","Error ocurred while signing up!"),HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/patientSignup")
    public ResponseEntity<?> PatientSignup(@RequestBody PatientDetails patientDetails) {
        try {
            patientRepo.save(patientDetails);
            return new ResponseEntity<>(Map.of("message", "Signup Successful!"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message", "Signup Failed!"), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/doctorSignup")
    public ResponseEntity<?> DoctorSignup(@RequestBody DoctorDetails doctorDetails) {
        try {
            doctorRepo.save(doctorDetails);
            return new ResponseEntity<>(Map.of("message","Signup Successful!"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message", "Signup Failed!"), HttpStatus.BAD_REQUEST);
        }
    }

}
