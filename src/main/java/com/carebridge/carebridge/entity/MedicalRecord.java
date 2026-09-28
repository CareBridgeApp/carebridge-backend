package com.carebridge.carebridge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "MedicalRecords")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {
    @Id
    private ObjectId id;
    private ObjectId patientId;
    private ObjectId doctorId;
    private ObjectId appointmentId;
    private LocalDateTime recordDate;
    private String diagnosis;
    private String notes;
}
