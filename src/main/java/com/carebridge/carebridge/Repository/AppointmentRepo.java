package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.entity.Appointments;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepo extends MongoRepository<Appointments, ObjectId> {
    List<Appointments> findByPatientId(ObjectId patientId);

    List<Appointments> findByDoctorId(ObjectId doctorId);

    Optional<Appointments> findByIdAndPatientId(
            ObjectId appointmentId,
            ObjectId patientId
    );

    Optional<Appointments> findByIdAndDoctorId(
            ObjectId appointmentId,
            ObjectId doctorId
    );
    boolean existsByDoctorIdAndAppointmentDate(
            ObjectId doctorId,
            LocalDateTime appointmentDate
    );
    List<Appointments> findByDoctorIdAndAppointmentDateBetween(
            ObjectId doctorId,
            LocalDateTime start,
            LocalDateTime end
    );
}
