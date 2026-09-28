package com.carebridge.carebridge.Dto;

import com.carebridge.carebridge.Utils.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class prescriptionResponse {
    private String id;
    private String medicalRecordId;
    private List<Medicine> medicines;
    private LocalDateTime createdAt;
}
