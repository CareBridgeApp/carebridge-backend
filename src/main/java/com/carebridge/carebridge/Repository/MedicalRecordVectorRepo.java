package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.entity.MedicalRecordVector;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MedicalRecordVectorRepo extends MongoRepository<MedicalRecordVector, ObjectId> {
    Optional<MedicalRecordVector>
    findByMedicalRecordId(ObjectId medicalRecordId);

}
