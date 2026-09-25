package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.Enum.RelationshipStatus;
import com.carebridge.carebridge.entity.doctorPatientRelationship;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DoctorPatientRelationshipRepo extends MongoRepository<doctorPatientRelationship, ObjectId> {
    Optional<doctorPatientRelationship>
    findByDoctorIdAndPatientId(
            ObjectId doctorId,
            ObjectId patientId
    );

    boolean existsByDoctorIdAndPatientIdAndStatus(
            ObjectId doctorId,
            ObjectId patientId,
            RelationshipStatus status
    );
}
