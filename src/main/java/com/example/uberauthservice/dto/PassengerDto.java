package com.example.uberauthservice.dto;


import com.example.uberprojectentityservice.models.Passenger;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDto {
    private Long id;
    private String name;

    private String email;

    private String password;

    private String phoneNumber;

    private Date createdAt;

    public static PassengerDto from(Passenger p){
        PassengerDto result=PassengerDto.builder()
                .id(p.getId())
                .createdAt(p.getCreatedAt())
                .name(p.getName())
                .email(p.getEmail())
                .password(p.getPassword())
                .phoneNumber(p.getPhoneNumber())
                .build();
        return result;
    }
}
