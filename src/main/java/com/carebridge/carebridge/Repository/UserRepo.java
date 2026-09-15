package com.carebridge.carebridge.Repository;

import com.carebridge.carebridge.entity.UserDetails;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<UserDetails, ObjectId> {
    UserDetails findByEmail(String email);
    Optional<UserDetails> findById(ObjectId id);
}
