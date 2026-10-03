package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.entity.Prescription;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PrescriptionRepo extends MongoRepository<Prescription, ObjectId> {
    Prescription findByMedicalRecordId(
            ObjectId medicalRecordId
    );
    List<Prescription> findByPatientId(
            ObjectId patientId
    );
}
