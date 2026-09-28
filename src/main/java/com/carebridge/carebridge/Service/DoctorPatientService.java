package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Dto.AppointmentRequestDTO;
import com.carebridge.carebridge.Enum.AccessLevel;
import com.carebridge.carebridge.Enum.RelationshipStatus;
import com.carebridge.carebridge.Enum.RelationshipType;
import com.carebridge.carebridge.Repository.DoctorPatientRelationshipRepo;
import com.carebridge.carebridge.Repository.PatientRepo;
import com.carebridge.carebridge.entity.PatientDetails;
import com.carebridge.carebridge.entity.doctorPatientRelationship;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DoctorPatientService {
    @Autowired
    private DoctorPatientRelationshipRepo relationshipRepo;
    @Autowired
    private PatientRepo patientRepo;
    public doctorPatientRelationship createRelationship(AppointmentRequestDTO request, ObjectId patientId) {
        doctorPatientRelationship doctorPatientRelationship = new doctorPatientRelationship();
        doctorPatientRelationship.setDoctorId(request.getDoctorId());
        doctorPatientRelationship.setPatientId(patientId);
        doctorPatientRelationship.setAccessLevel(AccessLevel.CONSULTATION_ONLY);
        doctorPatientRelationship.setRelationshipType(RelationshipType.TREATING_DOCTOR);
        doctorPatientRelationship.setStartedAt(LocalDateTime.now());
        doctorPatientRelationship.setStatus(RelationshipStatus.ACTIVE);
        relationshipRepo.save(doctorPatientRelationship);
        return doctorPatientRelationship;
    }
    public boolean validateRelationship(ObjectId patientId, ObjectId DoctorId){
        return relationshipRepo.existsByDoctorIdAndPatientIdAndStatus(patientId,DoctorId, RelationshipStatus.ACTIVE);
    }
    public void approveRelationship(ObjectId relationshipId, ObjectId userId){
        doctorPatientRelationship relationship=relationshipRepo.findById(relationshipId).orElseThrow(
                ()->new RuntimeException("Relationship Not Found")
        );
        PatientDetails patientDetails = patientRepo.findByUserid(userId);
        if(patientDetails==null){
            throw new RuntimeException("Patient Not Found");
        }
        if(!relationship.getPatientId().equals(patientDetails.getId())){
            throw new RuntimeException("You cannot grant access to this doctor!");
        }
        if(relationship.getAccessLevel()==AccessLevel.FULL_HISTORY){
            throw new RuntimeException("You already granted access to this doctor!");
        }
        relationship.setAccessLevel(AccessLevel.FULL_HISTORY);
        relationshipRepo.save(relationship);
    }
    public void revokeRelationship(ObjectId relationshipId, ObjectId userId){
        doctorPatientRelationship relationship=relationshipRepo.findById(relationshipId).orElseThrow(
                ()->new RuntimeException("Relationship Not Found")
        );
        PatientDetails patientDetails = patientRepo.findByUserid(userId);
        if(patientDetails==null){
            throw new RuntimeException("Patient Not Found");
        }
        relationship.setAccessLevel(AccessLevel.CONSULTATION_ONLY);
        relationshipRepo.save(relationship);
    }
}
