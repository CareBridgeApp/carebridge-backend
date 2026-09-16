package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.Service.DoctorSearchService;
import com.carebridge.carebridge.Service.DoctorService;
import com.carebridge.carebridge.Dto.DoctorRequestDTO;
import com.carebridge.carebridge.Dto.DoctorResponseDTO;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.UserDetails;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctor")
@Slf4j
public class DoctorController {
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private DoctorSearchService doctorSearchService;
    @GetMapping("/getprofile")
    public ResponseEntity<?> getdoctordetails(DoctorDetails doctorDetails){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            DoctorDetails patient = doctorRepo.findByUserid(id);
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("doctorMessage","error while getting profile"), HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updatedoctordetails(DoctorDetails doctorDetails){
        doctorRepo.save(doctorDetails);
        return new ResponseEntity<>(doctorDetails, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletedoctordetails(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userid = userDetails.getId();
            doctorService.deletedoctor(userid);
            return new ResponseEntity<>(Map.of("doctorMessage","Account Details deleted!"),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("doctorMessage","error while deleting doctordetails!"), HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/search")
    public ResponseEntity<List<DoctorResponseDTO>> searchdoctordetails(@RequestBody DoctorRequestDTO requestDTO){
        try{
            List<DoctorResponseDTO> doctors=doctorSearchService.doctorSearch(requestDTO);
            return new ResponseEntity<>(doctors, HttpStatus.OK);
        }catch (Exception e){
            log.error("error while searching doctordetails!",e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
                    doctorService.uploadProfilePicture(file, id);

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
}
