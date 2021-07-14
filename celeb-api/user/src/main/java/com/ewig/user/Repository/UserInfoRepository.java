package com.ewig.user.Repository;

import com.ewig.user.Document.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends MongoRepository<UserInfo, String> {


    @Query(value ="{'userId':?0,'otpType':?1}")

    UserInfo findbyUserId(String otpType,String userId);

    @Query(value = "{'userId':?0, 'otpType': ?1}")
    UserInfo findUserId(String userId, String otpType);

}
