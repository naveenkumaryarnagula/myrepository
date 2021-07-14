package com.ewig.user.Repository;

import com.ewig.user.Document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface   UserRepository extends MongoRepository<User, String> {

       @Query("{'phoneNumber': ?0}")
       List<String> findAllPhoneNumber(String phoneNumber);

       @Query("{'email': ?0}")
       List<String> findAllEmails(String email);

       @Query(value = "{'phoneNumber': ?0}")
       User finduserIdInUserInfo(String phoneNumber);

       @Query(value = "{'email': ?0}")
       User finduserIdInUser(String email);

       User findByEmail(String email);
}
