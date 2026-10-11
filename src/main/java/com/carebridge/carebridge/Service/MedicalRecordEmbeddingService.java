package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.MedicalRecord;
import org.bson.types.ObjectId;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MedicalRecordEmbeddingService {
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private DoctorRepo doctorRepo;
    public void saveMedicalEmbeddings(MedicalRecord medicalRecord) {
        DoctorDetails doctor=doctorRepo.findById(medicalRecord.getDoctorId()).orElse(null);
        String name="";
        if(doctor!=null) {
            name = doctor.getName();
        }
        String content =
                """
                Diagnosis: %s
                Notes: %s
                Date: %s
                "Doctor name: %s
                """.formatted(
                        medicalRecord.getDiagnosis(),
                        medicalRecord.getNotes(),
                        medicalRecord.getRecordDate(),
                        name
                );
        Document document=new Document(content,
                Map.of("medicalRecordId", medicalRecord.getId().toString(),"patientId", medicalRecord.getPatientId().toString())
        );
        vectorStore.add(List.of(document));
    }
    public String getDataFromMedicalRecord(String query, ObjectId patientId) {
        SearchRequest request=SearchRequest.builder()
                .query(query)
                .topK(3)
                .filterExpression("patientId == '" +
                        patientId.toString() +
                        "'")
                .build();
        List<Document> documents=vectorStore.similaritySearch(request);
        if(documents.size()==0){
            return "No documents found";
        }
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

    }
}
