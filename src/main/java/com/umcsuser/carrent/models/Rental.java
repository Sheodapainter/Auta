package com.umcsuser.carrent.models;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(name = "rental")
public class Rental {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rent_date", nullable = false)
    private String rentDateTime;

    @Column(name = "return_date")
    private String returnDateTime;

    public Rental copy() {
        return Rental.builder()
                .id(id)
                .vehicle(vehicle)
                .user(user)
                .rentDateTime(rentDateTime)
                .returnDateTime(returnDateTime)
                .build();
    }

    public boolean isActive() {
        return returnDateTime == null || returnDateTime.isBlank();
    }
    public String getVehicleId() { return vehicle == null ? null : vehicle.getId(); }
    public String getUserId() { return user == null ? null : user.getId(); }
}