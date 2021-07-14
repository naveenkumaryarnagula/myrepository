package com.ewig.celeb.Service;

import com.ewig.celeb.Document.Celebrity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomCelebrityDetails implements UserDetails {

    private Celebrity celebrity;


    public CustomCelebrityDetails(Celebrity celebrity) {
        this.celebrity= celebrity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return celebrity.getPassword();
    }

    @Override
    public String getUsername() {
        return celebrity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
