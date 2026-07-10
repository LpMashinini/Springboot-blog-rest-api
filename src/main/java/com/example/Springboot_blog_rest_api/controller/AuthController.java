package com.example.Springboot_blog_rest_api.controller;

import com.example.Springboot_blog_rest_api.dto.LoginDto;
import com.example.Springboot_blog_rest_api.dto.SignUpDto;
import com.example.Springboot_blog_rest_api.entity.Role;
import com.example.Springboot_blog_rest_api.entity.User;
import com.example.Springboot_blog_rest_api.repository.RoleRepository;
import com.example.Springboot_blog_rest_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){

        // new UsernamePasswordAuthenticationToken store user credentials
        // authenticationManager receives and authenticate the credentials
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()
        ));

        //stores the authenticated credentials from authentication manager
        // tells spring security that user is already logged in
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User signed in successfully", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){

        // check if username already exists
        if (userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>("Username is already taken : ", HttpStatus.BAD_REQUEST);
        }

        // checks if email already exists
        if (userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Email is already taken:", HttpStatus.BAD_REQUEST);
        }

        // create user object
        User  user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getPassword());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Collections.singleton(roles));

        // save data inside the database
        userRepository.save(user);

        return new ResponseEntity<>("User registered successfuly", HttpStatus.OK);

    }


}
