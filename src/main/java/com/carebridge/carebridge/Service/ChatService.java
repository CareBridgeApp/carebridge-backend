package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Enum.AccessLevel;
import com.carebridge.carebridge.Enum.IntentType;
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
    @Autowired
    private IntentClassificationService classificationService;

    @Value("classpath:Prompts/DoctorRecordAssistantPrompt.st")
    private Resource doctorPrompt;

    @Value("classpath:Prompts/GeneralAssistantPrompt.st")
    private Resource generalPrompt;

    @Value("classpath:Prompts/HealthAssistantPrompt.st")
    private Resource healthPrompt;

    @Value("classpath:Prompts/PatientRecordAssistantPrompt.st")
    private Resource patientPrompt;
    public Flux<String> askMyRecords(String questions,ObjectId userId){
        PatientDetails patient=patientRepo.findByUserid(userId);
        if(patient==null){
            throw new RuntimeException("You need to login first!");
        }
        ObjectId patientId=patient.getId();
        String conversation_id=patientId.toString();
        if(classificationService.classify(questions).equals(IntentType.GENERAL)){
            return chatClient
                    .prompt()
                    .system(system -> system
                            .text(generalPrompt)
                    )
                    .user(questions)
                    .advisors(advisor->advisor.param(ChatMemory.CONVERSATION_ID,conversation_id))
                    .stream()
                    .content();
        }
        if(classificationService.classify(questions).equals(IntentType.HEALTH)){
            return chatClient
                    .prompt()
                    .system(system -> system
                            .text(healthPrompt)
                    )
                    .user(questions)
                    .advisors(advisor->advisor.param(ChatMemory.CONVERSATION_ID,conversation_id))
                    .stream()
                    .content();
        }
        String context= medicalRecordEmbeddingService.getDataFromMedicalRecord(questions,patientId);
        return chatClient
                .prompt()
                .system(system -> system
                        .text(patientPrompt)
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
        String conversation_id= patientId.toString()+"_"+doctorId.toString();
        if(classificationService.classify(questions).equals(IntentType.GENERAL)){
            return chatClient
                    .prompt()
                    .system(system -> system
                            .text(generalPrompt)
                    )
                    .user(questions)
                    .advisors(advisor->advisor.param(ChatMemory.CONVERSATION_ID,conversation_id))
                    .stream()
                    .content();
        }
        if(classificationService.classify(questions).equals(IntentType.HEALTH)){
            return chatClient
                    .prompt()
                    .system(system -> system
                            .text(healthPrompt)
                    )
                    .user(questions)
                    .advisors(advisor->advisor.param(ChatMemory.CONVERSATION_ID,conversation_id))
                    .stream()
                    .content();
        }
        String context= medicalRecordEmbeddingService.getDataFromMedicalRecord(questions,patientId);
        return chatClient
                .prompt()
                .system(system -> system
                        .text(doctorPrompt)
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
