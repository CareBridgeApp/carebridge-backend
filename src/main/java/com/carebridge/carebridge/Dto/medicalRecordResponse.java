package com.carebridge.carebridge.Dto;

import com.carebridge.carebridge.entity.MedicalRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class medicalRecordResponse {
    private String medicalRecordId;
    private String diagnosis;
    private String notes;
    private LocalDateTime recordDate;
    private String title;
    private List<String> testsDone;
}
