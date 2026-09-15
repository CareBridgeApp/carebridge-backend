package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.entity.DoctorDetails;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoctorRepo extends MongoRepository<DoctorDetails, ObjectId> {
    DoctorDetails findByUserid(ObjectId userid);
}
