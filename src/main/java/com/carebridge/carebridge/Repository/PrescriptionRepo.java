package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.entity.Prescription;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrescriptionRepo extends MongoRepository<Prescription, ObjectId> {
    Prescription findByMedicalRecordId(
            ObjectId medicalRecordId
    );
}
