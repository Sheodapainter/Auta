package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.HibernateConfig;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.impl.RentalHibernateRepository;
import com.umcsuser.carrent.repositories.impl.VehicleHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateService implements VehicleServiceInterface{
    private final VehicleValidator vehicleValidator;
    private final VehicleHibernateRepository vehicleRepository;
    private final RentalHibernateRepository rentalRepository;

    public VehicleHibernateService(VehicleValidator vehicleValidator, VehicleHibernateRepository vehicleRepository, RentalHibernateRepository rentalRepository) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            return vehicleRepository.findAll();
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            List<Rental> rented = rentalRepository.findAll().stream().filter(r -> r.getReturnDateTime()==null||r.getReturnDateTime().isBlank()).toList();
            List<Vehicle> vehicles = vehicleRepository.findAll();
            for (Rental r : rented) {
                Vehicle v = r.getVehicle();
                vehicles.remove(v);
            }
            return vehicles;
        }
    }

    @Override
    public Vehicle findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            Optional<Vehicle> v = vehicleRepository.findById(id);
            return v.orElse(null);
        }
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            vehicleValidator.validate(vehicle);
            vehicleRepository.save(vehicle);
            tx.commit();
            return vehicle;
        } catch (RuntimeException e) {
            rollback(tx);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeVehicle(String id) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            if (rentalRepository.findByVehicleIdAndReturnDateIsNull(id).isEmpty()) {
                vehicleRepository.deleteById(id);
            } else {
                throw new IllegalArgumentException("Nie można usunąć wypożyczonego pojazdu!");
            }
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            Optional<Rental> r = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
            return r.isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepository.setSession(session);
        vehicleRepository.setSession(session);
    }
    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
