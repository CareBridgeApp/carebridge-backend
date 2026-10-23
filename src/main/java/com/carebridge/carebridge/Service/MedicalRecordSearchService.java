package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Dto.medicalRecordResponse;
import com.carebridge.carebridge.Enum.AccessLevel;
import com.carebridge.carebridge.Repository.DoctorPatientRelationshipRepo;
import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.MedicalRecordRepo;
import com.carebridge.carebridge.Repository.PatientRepo;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.MedicalRecord;
import com.carebridge.carebridge.entity.PatientDetails;
import com.carebridge.carebridge.entity.doctorPatientRelationship;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class MedicalRecordSearchService {
    private final MongoTemplate mongoTemplate;
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private MedicalRecordRepo medicalRecordRepo;
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private DoctorPatientRelationshipRepo relationshipRepo;
    public MedicalRecordSearchService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    private String buildTitleRegex(String title) {
        StringBuilder regex = new StringBuilder();

        for (char c : title.trim().toCharArray()) {
            if (Character.isWhitespace(c) || c == '-') {
                continue;
            }

            if ("\\.[]{}()*+?^$|".indexOf(c) >= 0) {
                regex.append('\\');
            }

            regex.append(c).append("[\\s-]*");
        }

        return regex.toString();
    }

    public List<medicalRecordResponse> searchByTitleForPatient(ObjectId userId, String title){
        if(title==null || title.isBlank()){
            throw new RuntimeException("Title cannot be null or blank");
        }
        PatientDetails patientDetails=patientRepo.findByUserid(userId);
        if(patientDetails==null){
            throw new RuntimeException("You are not authorized to view this Patients medical records");
        }
        ObjectId patientId=patientDetails.getId();
        Query query = new Query();
        query.addCriteria(Criteria.where("patientId").is(patientId)
        .and("title").regex(buildTitleRegex(title),"i"));
        List<MedicalRecord> records = mongoTemplate.find(query, MedicalRecord.class);
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
    public List<medicalRecordResponse>searchByTitleForDoctor(ObjectId userId,ObjectId patientId,String title){
        if(title==null || title.isBlank()){
            throw new RuntimeException("Title cannot be null or blank");
        }
        DoctorDetails doctorDetails=doctorRepo.findByUserid(userId);
        if(doctorDetails==null){
            throw new RuntimeException("You are not authorized to view this Patients medical records");
        }
        ObjectId doctorId=doctorDetails.getId();
        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(doctorId,patientId).get();
        if(relationship==null){
            throw new RuntimeException("You are not authorized to view this Patients medical records");
        }
        if(!relationship.getDoctorId().equals(doctorId)){
            throw new RuntimeException("You are not authorized to view this Patients medical records");
        }
        Query query = new Query();
        if(relationship.getAccessLevel().equals(AccessLevel.CONSULTATION_ONLY)){
            query.addCriteria(Criteria.where("doctorId").is(doctorId).and("patientId").is(patientId)
                    .and("title").regex(buildTitleRegex(title),"i"));
            List<MedicalRecord> records = mongoTemplate.find(query, MedicalRecord.class);
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
        query.addCriteria(Criteria.where("patientId").is(patientId).and("title").regex(buildTitleRegex(title),"i"));
        List<MedicalRecord>records=mongoTemplate.find(query, MedicalRecord.class);
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

}
