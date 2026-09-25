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
        return medicalRecordRepo.save(medicalRecord);
    }
    public List<MedicalRecord> getMyMedicalRecords(ObjectId userId) {
        PatientDetails patient=patientRepo.findByUserid(userId);
        return medicalRecordRepo.findByPatientId(patient.getId());
    }
    public MedicalRecord getMedicalRecord(ObjectId medicalRecordId,ObjectId userId) {
        MedicalRecord record=medicalRecordRepo.findById(medicalRecordId).orElseThrow(()->new RuntimeException("medical record not found"));
        if(record.getPatientId().equals(userId)){
            return record;
        }
        throw new RuntimeException("medical record does not belong to this Patient");
    }
    public List<MedicalRecord> getAllPatientsMedicalRecords(ObjectId patientId,ObjectId userId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        ObjectId doctorId=doctor.getId();
        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(doctorId,patientId).get();
        if(!relationship.getDoctorId().equals(doctorId)){
            throw new RuntimeException("You are not authorized to view this Patients medical records");
        }
        if(relationship.getAccessLevel()== AccessLevel.CONSULTATION_ONLY){
            throw new RuntimeException("You can only view the medical records associated with you!");
        }
        List<MedicalRecord>records=medicalRecordRepo.findByPatientId(patientId);
        if(records.isEmpty()){
            throw new RuntimeException("No Medical records found for this Patient");
        }
        return records;
    }

    public List<MedicalRecord> getMedicalRecords(ObjectId patientId,ObjectId userId) {
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        ObjectId doctorId=doctor.getId();
        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(doctorId,patientId).get();
        if(!relationship.getDoctorId().equals(doctorId)){
            throw new RuntimeException("You are not authorized to view this Patients medical records");
        }
        List<MedicalRecord>records=medicalRecordRepo.findByDoctorIdAndPatientId(doctorId,patientId);
        if(records.isEmpty()){
            throw new RuntimeException("No Medical records found for this Patient");
        }
        return records;
    }
    @Transactional
    public MedicalRecord updateMedicalRecord(ObjectId medicalRecordId,MedicalRecordRequest request,ObjectId userId) {
        MedicalRecord record=medicalRecordRepo.findById(medicalRecordId).orElseThrow(()->new RuntimeException("medical record not found"));
        //verify the looged in user
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        if(!record.getDoctorId().equals(doctor.getId())){
            throw new RuntimeException("You are not allowed to update this record!");
        }
        record.setDiagnosis(request.getDiagnosis());
        record.setNotes(request.getNotes());
        record.setRecordDate(LocalDateTime.now());
        return medicalRecordRepo.save(record);
    }
}
