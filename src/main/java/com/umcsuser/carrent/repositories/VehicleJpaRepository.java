package com.umcsuser.carrent.repositories;

import com.umcsuser.carrent.models.Vehicle;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("jpa")
public interface VehicleJpaRepository extends JpaRepository<Vehicle, String> {
}
