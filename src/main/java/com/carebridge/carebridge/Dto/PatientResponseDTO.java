package com.carebridge.carebridge.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponseDTO {
    private ObjectId id;
    private String name;
    private String gender;
    private String phone;
    private String state;
    private String city;
    private int age;
}
