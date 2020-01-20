package com.open.poker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.open.poker.schema.SignupRequest;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Optional;

@Entity
@Table(name = "userprofile")
@Value
@Builder
public class UserProfile  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @NonNull
    @With
    @NotBlank
    @Size(min = 4, max = 20)
    private final String username;
    @NonNull
    @With
    @JsonIgnore
    @NotBlank
    @Size(min = 8, max = 20)
    private final String password;
    @NonNull
    @With
    @JsonIgnore
    @Email
    @Size(min = 8, max = 40)
    private final String email;
    @NonNull
    @With
    @NotBlank
    @Size(min = 1, max = 20)
    @Column(columnDefinition = "firstName")
    private final String firstName;
    @NonNull
    @With
    @NotBlank
    @Size(min = 1, max = 20)
    @Column(columnDefinition = "lastName")
    private final String lastName;


    public static UserProfile of(final SignupRequest request) {
        return new UserProfile.UserProfileBuilder().email(request.getEmail()).username(request.getUsername())
                .password(request.getPassword()).firstName(Optional.ofNullable(request.getFirstName()).orElse(request.getUsername()))
                .lastName(Optional.ofNullable(request.getLastName()).orElse(request.getUsername())).build();
    }

    public User toUser() {
        return new User(username, password, Collections.emptyList());
    }
}
