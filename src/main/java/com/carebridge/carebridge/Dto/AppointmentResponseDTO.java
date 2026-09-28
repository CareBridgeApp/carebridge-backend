package com.carebridge.carebridge.Dto;

import com.carebridge.carebridge.Enum.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {
    String appointmentId;
    AppointmentStatus appointmentStatus;
    private LocalDateTime appointmentDate;
    private String reason;
}
