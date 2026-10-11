package com.carebridge.carebridge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "medical_record_vectors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordVector {
    @Id
    private ObjectId id;
    private ObjectId medicalRecordId;
    private String content;
    private List<Double> embedding;
    private LocalDateTime recordDate;
}
