package org.lupoi.http_test.repository;/*
    @author user
    @project HTTP_Test
    @class UserRepository
    @version 1.0.0
    @since 10.04.2025 - 12.58
*/


import org.lupoi.http_test.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {
}
