package com.carebridge.carebridge.entity;

import com.carebridge.carebridge.Utils.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "prescriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {

    @Id
    private ObjectId id;

    private ObjectId medicalRecordId;
    private ObjectId doctorId;
    private ObjectId patientId;

    private List<Medicine> medicines;

    private LocalDateTime createdAt;
}
