package com.carebridge.carebridge.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "PatientDetails")
public class PatientDetails {
    @Id
    private ObjectId id;
    private ObjectId userid;
    private String name;
    private String gender;
    private String phone;
    private LocalDate dateOfBirth;
    private String state;
    private String city;
    private String profilePictureUrl;
}
