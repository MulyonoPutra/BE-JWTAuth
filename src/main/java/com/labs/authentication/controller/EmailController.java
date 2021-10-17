package com.labs.authentication.controller;


import com.labs.authentication.dto.ChangePasswordDTO;
import com.labs.authentication.dto.EmailValuesDTO;
import com.labs.authentication.dto.Messages;
import com.labs.authentication.entity.Users;
import com.labs.authentication.service.EmailService;
import com.labs.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/email-password")
@CrossOrigin
public class EmailController {

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String mailFrom;

    private static final String subject = "Forgot Password";


    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmailTemplate(@RequestBody EmailValuesDTO dto) {
        Optional<Users> usersOptional = userService.getByNameUserOrEmail(dto.getMailTo());
        if(!usersOptional.isPresent()) {
            return new ResponseEntity(new Messages("There is no user with those credentials"), HttpStatus.NOT_FOUND);
        }
        Users users = usersOptional.get();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(users.getEmail());
        dto.setSubject(subject);
        dto.setUserName(users.getUsername());
        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);
        users.setTokenPassword(tokenPassword);
        userService.save(users);
        emailService.sendEmail(dto);
        return new ResponseEntity(new Messages("We have sent you an email"), HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new Messages("Bad Request!"), HttpStatus.BAD_REQUEST);
        }

        if(!dto.getPassword().equals(dto.getConfirmPassword())) {
            return new ResponseEntity(new Messages("Passwords do not match"), HttpStatus.BAD_REQUEST);
        }

        Optional<Users> usersOptional = userService.getByTokenPassword(dto.getTokenPassword());

        if(!usersOptional.isPresent()) {
            return new ResponseEntity(new Messages("There is no user with those credentials"), HttpStatus.NOT_FOUND);
        }
        Users users = usersOptional.get();
        String newPassword = passwordEncoder.encode(dto.getPassword());
        users.setPassword(newPassword);
        users.setTokenPassword(null);
        userService.save(users);
        return new ResponseEntity(new Messages("Updated password"), HttpStatus.OK);
    }
}
