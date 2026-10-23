package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Dto.MedicalRecordRequest;
import com.carebridge.carebridge.Enum.AccessLevel;
import com.carebridge.carebridge.Enum.AppointmentStatus;
import com.carebridge.carebridge.Repository.*;
import com.carebridge.carebridge.entity.Appointments;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.MedicalRecord;
import com.carebridge.carebridge.entity.PatientDetails;
import com.carebridge.carebridge.entity.doctorPatientRelationship;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.carebridge.carebridge.Dto.medicalRecordResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepo medicalRecordRepo;
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private DoctorPatientRelationshipRepo relationshipRepo;
    @Autowired
    private MedicalRecordEmbeddingService medicalRecordEmbeddingService;
    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecordRequest request,ObjectId userId) {
        //Find Appointment
        ObjectId appointmentId = new ObjectId(request.getAppointmentId());
        Appointments appointment=appointmentRepo.findById(appointmentId).orElseThrow(()->new RuntimeException("appointment not found"));
        //Check if doctor is real
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        if(!appointment.getDoctorId().equals(doctor.getId())){
            throw new RuntimeException("You are not the doctor assigned to this appointment");
        }
        //Check the status
        if(appointment.getStatus()!= AppointmentStatus.COMPLETED){
            throw new RuntimeException(
                    "Medical record can only be created after appointment is completed"
            );
        }
        //Check if record exists
        if(medicalRecordRepo.findByAppointmentId(appointment.getId())!=null){
            throw new RuntimeException("Medical record already exists for this appointment");
        }
        MedicalRecord  medicalRecord=new MedicalRecord();
        medicalRecord.setRecordDate(LocalDateTime.now());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setAppointmentId(appointmentId);
        medicalRecord.setNotes(request.getNotes());
        medicalRecord.setPatientId(appointment.getPatientId());
        medicalRecord.setDoctorId(appointment.getDoctorId());
        medicalRecord.setTestsDone(request.getTestsDone());
        medicalRecord.setTitle(request.getTitle());
        medicalRecord=medicalRecordRepo.save(medicalRecord);
        medicalRecordEmbeddingService.saveMedicalEmbeddings(medicalRecord);
        return medicalRecord;
    }
    public List<medicalRecordResponse> getMyMedicalRecords(ObjectId userId) {
        PatientDetails patient=patientRepo.findByUserid(userId);
        List<MedicalRecord> records=medicalRecordRepo.findByPatientId(patient.getId());
        return records.stream().map(record->{
            medicalRecordResponse dto=new medicalRecordResponse();
            dto.setMedicalRecordId(record.getId().toHexString());
            dto.setRecordDate(record.getRecordDate());
            dto.setDiagnosis(record.getDiagnosis());
            dto.setNotes(record.getNotes());
            dto.setTestsDone(record.getTestsDone());
            dto.setTitle(record.getTitle());
            return dto;
        }).toList();
    }
    public List<medicalRecordResponse> getAllPatientsMedicalRecords(ObjectId patientId,ObjectId userId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        ObjectId doctorId=doctor.getId();
        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(doctorId,patientId).get();
        if(relationship==null){
            throw new RuntimeException("You are not authorized to view this Patients medical records");
        }
        if(relationship.getAccessLevel()== AccessLevel.CONSULTATION_ONLY){
            throw new RuntimeException("You can only view the medical records associated with you!");
        }
        List<MedicalRecord>records=medicalRecordRepo.findByPatientId(patientId);
        if(records.isEmpty()){
            throw new RuntimeException("No Medical records found for this Patient");
        }
        return records.stream().map(record->{
            medicalRecordResponse dto=new medicalRecordResponse();
            dto.setMedicalRecordId(record.getId().toHexString());
            dto.setRecordDate(record.getRecordDate());
            dto.setDiagnosis(record.getDiagnosis());
            dto.setNotes(record.getNotes());
            dto.setTestsDone(record.getTestsDone());
            dto.setTitle(record.getTitle());
            return dto;
        }).toList();
    }

    public List<medicalRecordResponse> getMedicalRecords(ObjectId patientId,ObjectId userId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        ObjectId doctorId=doctor.getId();
        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(doctorId,patientId).get();
        if(relationship==null){
            throw new RuntimeException("You are not authorized to view this Patients medical records");
        }
        List<MedicalRecord>records=medicalRecordRepo.findByDoctorIdAndPatientId(doctorId,patientId);
        if(records.isEmpty()){
            throw new RuntimeException("No Medical records found for this Patient");
        }
        return records.stream().map(record->{
            medicalRecordResponse dto=new medicalRecordResponse();
            dto.setMedicalRecordId(record.getId().toHexString());
            dto.setRecordDate(record.getRecordDate());
            dto.setDiagnosis(record.getDiagnosis());
            dto.setNotes(record.getNotes());
            dto.setTestsDone(record.getTestsDone());
            dto.setTitle(record.getTitle());
            return dto;
        }).toList();
    }
    @Transactional
    public medicalRecordResponse updateMedicalRecord(MedicalRecordRequest request,ObjectId userId,ObjectId medicalRecordId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        if(doctor==null){
            throw new RuntimeException("You are not authorized to update this record!");
        }
        ObjectId doctorId=doctor.getId();
        MedicalRecord record=medicalRecordRepo.findById(medicalRecordId).orElseThrow(()->new RuntimeException("medical record not found"));
        if(!record.getDoctorId().equals(doctor.getId())){
            throw new RuntimeException("You are not authorized to update this record!");
        }
        record.setDiagnosis(request.getDiagnosis());
        record.setNotes(request.getNotes());
        record.setRecordDate(LocalDateTime.now());
        record.setTitle(request.getTitle());
        record.setTestsDone(request.getTestsDone());
        medicalRecordRepo.save(record);
        medicalRecordResponse response=new medicalRecordResponse();
        response.setDiagnosis(record.getDiagnosis());
        response.setNotes(record.getNotes());
        response.setRecordDate(LocalDateTime.now());
        response.setMedicalRecordId(record.getId().toHexString());
        response.setTitle(request.getTitle());
        response.setTestsDone(request.getTestsDone());
        return response;
    }
//    public List<medicalRecordResponse> searchByTitleForPatient(ObjectId userId,String title){
//        if(title==null || title.isBlank()){
//            throw new RuntimeException("Title cannot be null or blank");
//        }
//        PatientDetails patientDetails=patientRepo.findByUserid(userId);
//        if(patientDetails==null){
//            throw new RuntimeException("You are not authorized to view this Patients medical records");
//        }
//        ObjectId patientId=patientDetails.getId();
//        List<MedicalRecord>records=medicalRecordRepo.findByPatientIdAndTitleContainingIgnoreCase(patientId,title);
//        if(records.isEmpty()){
//            throw new RuntimeException("No Medical records found for this Patient");
//        }
//        return records.stream().map(record->{
//            medicalRecordResponse dto=new medicalRecordResponse();
//            dto.setMedicalRecordId(record.getId().toHexString());
//            dto.setRecordDate(record.getRecordDate());
//            dto.setDiagnosis(record.getDiagnosis());
//            dto.setNotes(record.getNotes());
//            dto.setTestsDone(record.getTestsDone());
//            dto.setTitle(record.getTitle());
//            return dto;
//        }).toList();
//    }
//    public List<medicalRecordResponse>searchByTitleForDoctor(ObjectId userId,ObjectId patientId,String title){
//        if(title==null || title.isBlank()){
//            throw new RuntimeException("Title cannot be null or blank");
//        }
//        DoctorDetails doctorDetails=doctorRepo.findByUserid(userId);
//        if(doctorDetails==null){
//            throw new RuntimeException("You are not authorized to view this Patients medical records");
//        }
//        ObjectId doctorId=doctorDetails.getId();
//        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(doctorId,patientId).get();
//        if(relationship==null){
//            throw new RuntimeException("You are not authorized to view this Patients medical records");
//        }
//        if(!relationship.getDoctorId().equals(doctorId)){
//            throw new RuntimeException("You are not authorized to view this Patients medical records");
//        }
//        if(relationship.getAccessLevel().equals(AccessLevel.CONSULTATION_ONLY)){
//            List<MedicalRecord>records=medicalRecordRepo.findByPatientIdAndDoctorIdAndTitleContainingIgnoreCase(patientId,doctorId,title);
//            if(records.isEmpty()){
//                throw new RuntimeException("No Medical records found for this Patient");
//            }
//            return records.stream().map(record->{
//                medicalRecordResponse dto=new medicalRecordResponse();
//                dto.setMedicalRecordId(record.getId().toHexString());
//                dto.setRecordDate(record.getRecordDate());
//                dto.setDiagnosis(record.getDiagnosis());
//                dto.setNotes(record.getNotes());
//                dto.setTestsDone(record.getTestsDone());
//                dto.setTitle(record.getTitle());
//                return dto;
//            }).toList();
//        }
//        List<MedicalRecord>records=medicalRecordRepo.findByPatientIdAndTitleContainingIgnoreCase(patientId,title);
//        if(records.isEmpty()){
//            throw new RuntimeException("No Medical records found for this Patient");
//        }
//        return records.stream().map(record->{
//            medicalRecordResponse dto=new medicalRecordResponse();
//            dto.setMedicalRecordId(record.getId().toHexString());
//            dto.setRecordDate(record.getRecordDate());
//            dto.setDiagnosis(record.getDiagnosis());
//            dto.setNotes(record.getNotes());
//            dto.setTestsDone(record.getTestsDone());
//            dto.setTitle(record.getTitle());
//            return dto;
//        }).toList();
//    }
}
