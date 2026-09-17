package com.carebridge.carebridge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "DoctorDetails")
public class DoctorDetails {
    @Id
    private ObjectId id;
    private ObjectId userid;

    private String name;
    private String gender;
    private String specialization;
    private List<String> qualification;
    private String phone;
    private String state;
    private String city;
    private String profilePictureUrl;
    private String profilePicturePublicId;
}
