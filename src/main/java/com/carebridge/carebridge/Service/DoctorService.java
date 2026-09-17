package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.Utils.ImageValidator;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.PatientDetails;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

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

        Map<String,String> result= cloudinaryService.uploadProfilePicture(
                file,
                "carebridge/doctors"
        );

        doctor.setProfilePictureUrl(result.get("url"));
        doctor.setProfilePicturePublicId(result.get("publicId"));
        doctorRepo.save(doctor);

        return result.get("url");
    }
    public void deleteProfilePicture(ObjectId userId)
            throws IOException {

        DoctorDetails doctor = doctorRepo
                .findByUserid(userId);

        String publicId = doctor.getProfilePicturePublicId();

        if (publicId != null) {
            cloudinaryService.deleteProfilePicture(publicId);
        }

        doctor.setProfilePictureUrl(null);
        doctor.setProfilePicturePublicId(null);

        doctorRepo.save(doctor);
    }
}

