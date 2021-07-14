package com.ewig.celeb.Repository;

import com.ewig.celeb.Document.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByToken(String confirmationToken);
}
