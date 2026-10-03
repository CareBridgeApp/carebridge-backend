package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Dto.CreatePrescriptionRequest;
import com.carebridge.carebridge.Dto.prescriptionResponse;
import com.carebridge.carebridge.Enum.AccessLevel;
import com.carebridge.carebridge.Repository.*;
import com.carebridge.carebridge.entity.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrescriptionService {
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private PrescriptionRepo prescriptionRepo;
    @Autowired
    private MedicalRecordRepo medicalRecordRepo;
    @Autowired
    private DoctorPatientRelationshipRepo relationshipRepo;
    @Autowired
    private PatientRepo patientRepo;
    public prescriptionResponse createPrescription(CreatePrescriptionRequest request, ObjectId userId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        if(doctor==null) {
            throw new RuntimeException("Doctor Not Found");
        }
        ObjectId recordId=new ObjectId(request.getMedicalRecordId());
        MedicalRecord record=medicalRecordRepo.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Create a medical record to create this prescription"));
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
    public prescriptionResponse getPrescriptionByMedicalRecordId(ObjectId medicalRecordId) {
        Prescription prescription=prescriptionRepo.findByMedicalRecordId(medicalRecordId);
        if(prescription==null){
            throw new RuntimeException("Prescription Not Found for this Medical Record!");
        }
        prescriptionResponse response=new prescriptionResponse();
        response.setCreatedAt(prescription.getCreatedAt());
        response.setMedicalRecordId(prescription.getMedicalRecordId().toHexString());
        response.setMedicines(prescription.getMedicines());
        return response;
    }
    public List<prescriptionResponse> getPrescriptionByPatientId(ObjectId PatientId,ObjectId userId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        if(doctor==null){
            throw new RuntimeException("Doctor Not Found");
        }
        ObjectId doctorId=doctor.getId();
        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(doctorId,PatientId).get();
        if(relationship==null){
            throw new RuntimeException("You are not authorized to access this patient's prescription!");
        }
        if(relationship.getAccessLevel().equals(AccessLevel.CONSULTATION_ONLY)){
            throw new RuntimeException("You are not authorized to access the complete prescription history for this patient!");
        }
        List<Prescription>prescriptions=prescriptionRepo.findByPatientId(PatientId);
        return prescriptions.stream().map(prescription -> {
            prescriptionResponse dto=new prescriptionResponse();
            dto.setId(prescription.getId().toHexString());
            dto.setMedicalRecordId(prescription.getMedicalRecordId().toHexString());
            dto.setMedicines(prescription.getMedicines());
            dto.setCreatedAt(prescription.getCreatedAt());
            return dto;
        }).toList();
    }
    public List<prescriptionResponse> getMyPrescriptions(ObjectId userId) {
        PatientDetails patient=patientRepo.findByUserid(userId);
        if(patient==null){
            throw new RuntimeException("Patient Not Found");
        }
        List<Prescription>prescriptions=prescriptionRepo.findByPatientId(patient.getId());
        if(prescriptions==null){
            throw new RuntimeException("Prescriptions Not Found");
        }
        return prescriptions.stream().map(prescription -> {
            prescriptionResponse dto=new prescriptionResponse();
            dto.setId(prescription.getId().toHexString());
            dto.setMedicalRecordId(prescription.getMedicalRecordId().toHexString());
            dto.setMedicines(prescription.getMedicines());
            dto.setCreatedAt(prescription.getCreatedAt());
            return dto;
        }).toList();
    }
    public prescriptionResponse updatePrescription(CreatePrescriptionRequest request,ObjectId userId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        if(doctor==null){
            throw new RuntimeException("Doctor Not Found");
        }
        ObjectId doctorId=doctor.getId();
        ObjectId recordId=new ObjectId(request.getMedicalRecordId());
        Prescription prescription=prescriptionRepo.findByMedicalRecordId(recordId);
        if(prescription==null){
            throw new RuntimeException("First create the prescription");
        }
        if(!prescription.getDoctorId().equals(doctorId)){
            throw new RuntimeException("You are not authorized to update this patient's prescription!");
        }
        prescription.setMedicines(request.getMedicines());
        prescription.setCreatedAt(LocalDateTime.now());
        prescriptionRepo.save(prescription);
        prescriptionResponse response=new prescriptionResponse();
        response.setCreatedAt(prescription.getCreatedAt());
        response.setMedicalRecordId(prescription.getMedicalRecordId().toHexString());
        response.setMedicines(prescription.getMedicines());
        return response;
    }
}
