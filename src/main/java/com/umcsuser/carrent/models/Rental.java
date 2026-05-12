package com.umcsuser.carrent.models;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Rental {

    private String id;
    private String vehicleId;
    private String userId;
    private String rentDateTime;
    private String returnDateTime;

    public Rental copy() {
        return Rental.builder()
                .id(id)
                .vehicleId(vehicleId)
                .userId(userId)
                .rentDateTime(rentDateTime)
                .returnDateTime(returnDateTime)
                .build();
    }

    public boolean isActive() {
        return returnDateTime == null || returnDateTime.isBlank();
    }
}