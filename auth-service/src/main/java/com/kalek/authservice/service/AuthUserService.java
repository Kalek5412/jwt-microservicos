package com.kalek.authservice.service;

import com.kalek.authservice.dto.AuthUserDto;
import com.kalek.authservice.dto.TokenDto;
import com.kalek.authservice.entity.AuthUser;
import com.kalek.authservice.repository.AuthUserRepository;
import com.kalek.authservice.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthUserService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AuthUserRepository authUserRepository;

    public AuthUser save(AuthUserDto dto){
        Optional<AuthUser> userOp=authUserRepository.findByUsername(dto.getUsername());
        if(userOp.isPresent()){
            return null;}
        String password=passwordEncoder.encode(dto.getPassword());
        AuthUser authUser=AuthUser.builder().username(dto.getUsername()).password(password).build();
        return authUserRepository.save(authUser);
    }

    public TokenDto login(AuthUserDto dto){
        Optional<AuthUser> userOp=authUserRepository.findByUsername(dto.getUsername());
        if(!userOp.isPresent()){
            return null;
        }
        if(passwordEncoder.matches(dto.getPassword(), userOp.get().getPassword())){
            return new TokenDto(jwtProvider.createToken(userOp.get()));
        }
        return null;
    }

    public TokenDto validate(String token){
        if(!jwtProvider.validate(token)){
            return null;
        }
        String username= jwtProvider.getUsernameFromToken(token);
        if(!authUserRepository.findByUsername(username).isPresent()){
            return null;
        }
        return new TokenDto(token);
    }
}
