package com.carebridge.carebridge.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDTO {
    private String id;
    private String name;
    private String specialization;
    private List<String> qualification;
    private String state;
    private String city;
    private String phone;
    private String gender;

    public DoctorResponseDTO(String hexString, String name, String specialization, String state, String city, List<String> qualification, String phone, String gender) {
        this.id = hexString;
        this.name = name;
        this.specialization = specialization;
        this.state = state;
        this.city = city;
        this.qualification = qualification;
        this.phone = phone;
        this.gender = gender;
    }
}
