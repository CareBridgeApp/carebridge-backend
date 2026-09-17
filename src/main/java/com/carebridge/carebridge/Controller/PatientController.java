package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Repository.PatientRepo;
import com.carebridge.carebridge.Service.PatientService;
import com.carebridge.carebridge.Dto.PatientResponseDTO;
import com.carebridge.carebridge.entity.PatientDetails;
import com.carebridge.carebridge.entity.UserDetails;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private PatientService patientService;
    @GetMapping("/getprofile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            PatientResponseDTO patient=patientService.getpatient(id);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("message","error while getting profile"), HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updatePatient(PatientDetails patientDetails) {
        patientRepo.save(patientDetails);
        return new ResponseEntity<>(patientDetails, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePatient() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userid = userDetails.getId();
            patientService.deletepatient(userid);
            return new ResponseEntity<>(Map.of("patientMessage","Account Details deleted!"), HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(Map.of("patientMessage","Error while deleting account details"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(
            value = "/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file) throws IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            String imageUrl =
                    patientService.uploadProfilePicture(file, id);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Profile picture updated successfully!",
                            "profilePictureUrl", imageUrl
                    )
            );
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("message","Error while uploading profile picture")
                    , HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete/profile-picture")
    public ResponseEntity<?> deleteProfilePicture(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            patientService.deleteProfilePicture(id);
            return new ResponseEntity<>(Map.of("message", "Profile picture deleted successfully!"), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("error while deleting profile picture", HttpStatus.BAD_REQUEST);
        }
    }

}
