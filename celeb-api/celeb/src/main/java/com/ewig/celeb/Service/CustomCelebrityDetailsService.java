package com.ewig.celeb.Service;

import com.ewig.celeb.Document.Celebrity;
import com.ewig.celeb.Repository.CelebrityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomCelebrityDetailsService implements UserDetailsService {

    @Autowired
    private CelebrityRepository celebrityRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Celebrity celebrity = celebrityRepository.findByEmail(email);
        if(celebrity==null){
            throw new UsernameNotFoundException("user not found");
        }
        return new CustomCelebrityDetails(celebrity);
    }
}
