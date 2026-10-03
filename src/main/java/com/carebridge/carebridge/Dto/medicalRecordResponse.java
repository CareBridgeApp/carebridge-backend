package com.carebridge.carebridge.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class medicalRecordResponse {
    private String medicalRecordId;
    private String diagnosis;
    private String notes;
    private LocalDateTime recordDate;
}
