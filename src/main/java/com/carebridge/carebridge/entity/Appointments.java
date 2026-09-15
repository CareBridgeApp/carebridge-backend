package com.carebridge.carebridge.entity;

import com.carebridge.carebridge.Enum.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Appointments")
@CompoundIndex(
        name = "doctor_slot_unique",
        def = "{'doctorId': 1, 'appointmentDate': 1}",
        unique = true
)
public class Appointments {
    @Id
    private ObjectId id;

    private ObjectId patientId;
    private ObjectId doctorId;

    private LocalDateTime appointmentDate;

    private String reason;

    private AppointmentStatus status;

    private LocalDateTime createdAt;

}
