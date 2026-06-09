package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalJpaRepository;
import com.umcsuser.carrent.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class RentalRepositoryJpaAdapter implements RentalRepository {

    private final RentalJpaRepository delegate;

    public RentalRepositoryJpaAdapter(RentalJpaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Rental> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public Rental save(Rental rental) {
        if(rental.getId()==null) {
            rental.setId(UUID.randomUUID().toString());
        }
        return delegate.save(rental);
    }

    @Override
    public void deleteById(String id) {
        delegate.deleteById(id);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        Optional<Rental> rental = delegate.findAll().stream()
                .filter(r -> Objects.equals(r.getVehicleId(), vehicleId))
                .filter(r -> r.getReturnDateTime()==null).findFirst();
        return rental.flatMap(value -> delegate.findById(value.getId()));
    }
}
