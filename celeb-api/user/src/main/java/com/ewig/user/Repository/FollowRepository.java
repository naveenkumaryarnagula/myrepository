package com.ewig.user.Repository;

import com.ewig.user.Document.Follow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {

    @Query(value = "{'celebId':?0, 'userId':?1}")
    Follow findBy(String celeb_id, String id);

}
