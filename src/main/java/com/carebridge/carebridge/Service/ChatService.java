package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Enum.AccessLevel;
import com.carebridge.carebridge.Repository.DoctorPatientRelationshipRepo;
import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.PatientRepo;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.PatientDetails;
import org.bson.types.ObjectId;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import com.carebridge.carebridge.entity.doctorPatientRelationship;

@Service
public class ChatService {
    private final ChatClient chatClient;
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private MedicalRecordEmbeddingService medicalRecordEmbeddingService;
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private DoctorPatientRelationshipRepo relationshipRepo;
    @Value("classpath:/Prompts/LLMprompts_1.st")
    private Resource systemmessage1;
    @Value("classpath:/Prompts/LLMprompts.st")
    private Resource systemmessage2;
    public Flux<String> askMyRecords(String questions,ObjectId userId){
        PatientDetails patient=patientRepo.findByUserid(userId);
        if(patient==null){
            throw new RuntimeException("You need to login first!");
        }
        ObjectId patientId=patient.getId();
        String context= medicalRecordEmbeddingService.getDataFromMedicalRecord(questions,patientId);
        String conversation_id=patientId.toString();
        return chatClient
                .prompt()
                .system(system -> system
                        .text(systemmessage1)
                        .param("context", context)
                )
                .user(questions)
                .advisors(advisor->advisor.param(ChatMemory.CONVERSATION_ID,conversation_id))
                .stream()
                .content();

    }
    public Flux<String> askPatientRecords(String questions,ObjectId userId,ObjectId patientId){
        DoctorDetails doctor=doctorRepo.findByUserid(userId);
        if(doctor==null){
            throw new RuntimeException("You need to login first!");
        }
        ObjectId doctorId=doctor.getId();
        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(doctorId,patientId).get();
        if(relationship==null){
            throw new RuntimeException("You are not authorized to access this patient's medical records.");
        }
        if(relationship.getAccessLevel().equals(AccessLevel.CONSULTATION_ONLY)){
            throw new RuntimeException("You are not authorized to access this patient's medical records.");
        }
        String context= medicalRecordEmbeddingService.getDataFromMedicalRecord(questions,patientId);
        String conversation_id=patientId.toString()+"_"+doctorId.toString();
        return chatClient
                .prompt()
                .system(system -> system
                        .text(systemmessage2)
                        .param("context", context)
                )
                .user(questions)
                .advisors(advisor->advisor.param(ChatMemory.CONVERSATION_ID,conversation_id))
                .stream()
                .content();
    }
    public boolean allowDoctor(ObjectId userId,ObjectId PatientId){
        doctorPatientRelationship relationship=relationshipRepo.findByDoctorIdAndPatientId(userId,PatientId).get();
        if(relationship==null){
            throw new RuntimeException("You are not authorized to ask about this Patient's medical records.");
        }
        if(relationship.getAccessLevel().equals(AccessLevel.CONSULTATION_ONLY)){
            throw new RuntimeException("You are not authorized to ask about this Patient's medical records.");
        }
        return true;
    }
}
