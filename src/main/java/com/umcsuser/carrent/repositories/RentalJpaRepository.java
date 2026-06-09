package com.umcsuser.carrent.repositories;

import com.umcsuser.carrent.models.Rental;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("jpa")
public interface RentalJpaRepository extends JpaRepository<Rental, String> {
}
