package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Repository.PatientRepo;
import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.Dto.PatientResponseDTO;
import com.carebridge.carebridge.Utils.ImageValidator;
import com.carebridge.carebridge.entity.PatientDetails;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Service
@Slf4j
public class PatientService {
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CloudinaryService cloudinaryService;
    public void deletepatient(ObjectId userId) {
        try {
            userRepo.deleteById(userId);
            PatientDetails patientDetails = patientRepo.findByUserid(userId);
            patientRepo.delete(patientDetails);
        }catch (Exception e){
            log.error("error while deleting patientdetails!", e);
        }
    }
    public PatientResponseDTO  getpatient(ObjectId userId) {
        PatientDetails patient = patientRepo.findByUserid(userId);
        int age= Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getGender(),
                patient.getPhone(),
                patient.getState(),
                patient.getCity(),
                age
        );
    }
    public String uploadProfilePicture(
            MultipartFile file,
            ObjectId userid) throws IOException {

        ImageValidator.validate(file);

        PatientDetails patient = patientRepo
                .findByUserid(userid);
        Map<String,String> result = cloudinaryService.uploadProfilePicture(
                file,
                "carebridge/patients"
        );

        patient.setProfilePictureUrl(result.get("url") );
        patient.setProfilePicturePublicId(result.get("publicId"));

        patientRepo.save(patient);

        return result.get("url");
    }
    public void deleteProfilePicture(ObjectId userId)
            throws IOException {

        PatientDetails patient = patientRepo
                .findByUserid(userId);

        String publicId = patient.getProfilePicturePublicId();

        if (publicId != null) {
            cloudinaryService.deleteProfilePicture(publicId);
        }

        patient.setProfilePictureUrl(null);
        patient.setProfilePicturePublicId(null);

        patientRepo.save(patient);
    }

}
