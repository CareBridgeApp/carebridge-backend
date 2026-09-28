package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Dto.CreatePrescriptionRequest;
import com.carebridge.carebridge.Dto.prescriptionResponse;
import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.MedicalRecordRepo;
import com.carebridge.carebridge.Repository.PrescriptionRepo;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.MedicalRecord;
import com.carebridge.carebridge.entity.Prescription;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PrescriptionService {
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private PrescriptionRepo prescriptionRepo;
    @Autowired
    private MedicalRecordRepo medicalRecordRepo;
    public prescriptionResponse createPrescription(CreatePrescriptionRequest request, ObjectId userId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        if(doctor==null){
            throw new RuntimeException("Doctor Not Found");
        }
        MedicalRecord record=medicalRecordRepo.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new RuntimeException("Medical Record Not Found"));
        ObjectId doctorId=doctor.getId();
        if(!record.getId().equals(doctorId)){
            throw new RuntimeException("You are not authorized to add a prescription!");
        }
        ObjectId patientId=record.getPatientId();
        Prescription prescription=prescriptionRepo.findByMedicalRecordId(record.getId());
        if(prescription!=null){
            throw new RuntimeException("Prescription already exists for this Medical Record!");
        }
        prescription=new Prescription();
        prescription.setPatientId(patientId);
        prescription.setDoctorId(doctorId);
        prescription.setMedicalRecordId(record.getId());
        prescription.setMedicines(request.getMedicines());
        prescription.setCreatedAt(LocalDateTime.now());
        prescriptionRepo.save(prescription);
        prescriptionResponse response=new prescriptionResponse();
        response.setCreatedAt(prescription.getCreatedAt());
        response.setMedicalRecordId(prescription.getMedicalRecordId().toHexString());
        response.setId(prescription.getId().toHexString());
        response.setMedicines(prescription.getMedicines());
        return response;
    }
}
