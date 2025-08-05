package com.example.uberauthservice.repositories;

import com.example.uberauthservice.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Long> {


    Optional<Passenger> findPassengerByEmail(String username);
}
