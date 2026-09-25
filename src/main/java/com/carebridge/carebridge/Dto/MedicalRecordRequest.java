package com.carebridge.carebridge.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordRequest {
    private String appointmentId;
    private String diagnosis;
    private String notes;
}
