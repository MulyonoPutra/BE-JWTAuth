package com.labs.authentication.service;

import com.labs.authentication.entity.Users;
import com.labs.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Optional<Users> getByUsername(String name){
        return userRepository.findByUsername(name);
    }

    public Optional<Users> getByNameUserOrEmail(String nameOrEmail){
        return userRepository.findByUsernameOrEmail(nameOrEmail, nameOrEmail);
    }

    public Optional<Users> getByTokenPassword(String tokenPassword){
        return userRepository.findByTokenPassword(tokenPassword);
    }

    public boolean existsByNameUser(String name){
        return userRepository.existsByUsername(name);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void save(Users user){
        userRepository.save(user);
    }
}
