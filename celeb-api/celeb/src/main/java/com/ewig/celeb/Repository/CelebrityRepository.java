package com.ewig.celeb.Repository;

import com.ewig.celeb.Document.Celebrity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CelebrityRepository extends MongoRepository<Celebrity, String> {
    Celebrity findByPhoneNumber(String phoneNumber);

    Celebrity findByEmail(String email);

    @Query(value = "{'id':?0}")
    Celebrity findByCelebrityId(String tokenId);


    @Query(value = "{'firstname':}",fields = "{firstName:1}")
    List<Celebrity> findAllCelebrities();
}
