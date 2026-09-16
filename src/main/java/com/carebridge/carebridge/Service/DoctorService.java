package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.Utils.ImageValidator;
import com.carebridge.carebridge.entity.DoctorDetails;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class DoctorService {
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CloudinaryService cloudinaryService;
    public void deletedoctor(ObjectId userId){
        try {
            userRepo.deleteById(userId);
            DoctorDetails doctorDetails = doctorRepo.findByUserid(userId);
            doctorRepo.delete(doctorDetails);
        }catch (Exception e){
            log.error("Error occured while deleting DoctorDetails",e);
        }
    }
    public void updateDoctor(DoctorDetails doctorDetails) {
        doctorRepo.save(doctorDetails);
    }
    public String uploadProfilePicture(
            MultipartFile file,
            ObjectId userid) throws IOException {

        ImageValidator.validate(file);

        DoctorDetails doctor = doctorRepo
                .findByUserid(userid);

        String imageUrl = cloudinaryService.uploadProfilePicture(
                file,
                "carebridge/doctors"
        );

        doctor.setProfilePictureUrl(imageUrl);

        doctorRepo.save(doctor);

        return imageUrl;
    }
}

