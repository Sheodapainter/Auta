package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class RentalHibernateRepository implements RentalRepository {
    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public List<Rental> findAll() {
        return session.createQuery("FROM Rental", Rental.class).list();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(session.get(Rental.class, id));
    }

    @Override
    public Rental save(Rental rental) {
        return session.merge(rental);
    }

    @Override
    public void deleteById(String id) {
        Rental rental = session.get(Rental.class, id);

        if (rental!=null) {
            session.remove(rental);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        Query<Rental> query = session.createQuery("""
                FROM Rental r
                WHERE r.vehicle.id = :vehicleId
                AND r.returnDateTime IS NULL
                """, Rental.class);
        query.setParameter("vehicleId", vehicleId);

        return query.uniqueResultOptional();
    }
}
