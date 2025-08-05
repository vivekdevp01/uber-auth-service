package com.example.uberauthservice.services;

import com.example.uberauthservice.helpers.AuthPassengerDetails;
import com.example.uberauthservice.models.Passenger;
import com.example.uberauthservice.repositories.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

//this class is responsible for loading the user in the form of UserDetails object for auth

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private  PassengerRepository passengerRepository;
//    public UserDetailsServiceImpl(PassengerRepository passengerRepository) {
//        this.passengerRepository = passengerRepository;
//    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       Optional <Passenger> passenger=passengerRepository.findPassengerByEmail(email);
       if(passenger.isPresent()){
           return new AuthPassengerDetails(passenger.get());
       }
       else{
         throw new UsernameNotFoundException("Cannot find the passenger with that email"+email);
       }

    }
}
