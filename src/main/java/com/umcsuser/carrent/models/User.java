package com.umcsuser.carrent.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "passwordHash")
public class User {
    private String id;
    private String login;
    private String passwordHash;
    private Role role;
    public User copy() {
        return User.builder()
                .id(id)
                .login(login)
                .passwordHash(passwordHash)
                .role(role)
                .build();
    }
}
