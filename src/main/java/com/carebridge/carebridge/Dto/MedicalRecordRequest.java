package com.carebridge.carebridge.Dto;

import com.carebridge.carebridge.entity.MedicalRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordRequest {
    private String appointmentId;
    private String diagnosis;
    private String notes;
    private List<String> testsDone;
    private String title;
}
