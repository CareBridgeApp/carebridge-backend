package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.entity.PatientDetails;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepo extends MongoRepository<PatientDetails, ObjectId> {
    PatientDetails findByUserid(ObjectId userid);


}
