package com.example.uberauthservice.controllers;

import com.example.uberauthservice.dto.AuthRequestDto;
import com.example.uberauthservice.dto.PassengerDto;
import com.example.uberauthservice.dto.PassengerSignupRequestDto;
import com.example.uberauthservice.services.AuthService;
import com.example.uberauthservice.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${cookie.expiry}")
    private int cookieExpiry;

    private final AuthenticationManager authenticationManager;
    private AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup/passenger")
    public ResponseEntity<PassengerDto> signUp(@RequestBody PassengerSignupRequestDto  passengerSignupRequestDto) {
       PassengerDto response=authService.signupPassenger(passengerSignupRequestDto);
       return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signin/passenger")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDto authRequestDto, HttpServletResponse response) {

        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(),authRequestDto.getPassword()));
        if(authentication.isAuthenticated()){
            String jwtToken=jwtService.createToken(authRequestDto.getEmail());
            ResponseCookie cookie= ResponseCookie.from("jwtToken",jwtToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(cookieExpiry)
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE,cookie.toString());
            Map<String,Object> payload=new HashMap<>();
            payload.put("email",authRequestDto.getEmail());
            payload.put("password",authRequestDto.getPassword());

            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        }
        else{

        throw new UsernameNotFoundException("User not found");
        }
    }



}
