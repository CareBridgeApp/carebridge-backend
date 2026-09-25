package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.entity.MedicalRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepo extends MongoRepository<MedicalRecord, ObjectId> {
    MedicalRecord findByAppointmentId(ObjectId appointmentId);

    List<MedicalRecord> findByPatientId(ObjectId patientId);

    List<MedicalRecord> findByDoctorId(ObjectId doctorId);
    List<MedicalRecord> findByDoctorIdAndPatientId(ObjectId doctorId, ObjectId patientId);
}
