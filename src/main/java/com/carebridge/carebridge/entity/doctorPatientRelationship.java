package com.carebridge.carebridge.entity;

import com.carebridge.carebridge.Enum.AccessLevel;
import com.carebridge.carebridge.Enum.RelationshipStatus;
import com.carebridge.carebridge.Enum.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "doctorPatientRelationship")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class doctorPatientRelationship {
    @Id
    private ObjectId id;
    private ObjectId doctorId;
    private ObjectId patientId;
    private RelationshipType relationshipType;
    private RelationshipStatus status;
    private AccessLevel accessLevel;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;

}
