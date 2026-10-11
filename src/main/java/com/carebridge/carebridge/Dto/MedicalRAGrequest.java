package com.carebridge.carebridge.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRAGrequest {
    private String PatientId;
    private String question;
}
