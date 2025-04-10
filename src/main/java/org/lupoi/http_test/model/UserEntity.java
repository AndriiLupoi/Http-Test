package org.lupoi.http_test.model;/*
    @author user
    @project HTTP_Test
    @class User
    @version 1.0.0
    @since 10.04.2025 - 12.58
*/

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class UserEntity {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String number;

    public UserEntity(String id, String firstName, String lastName, String number) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
    }
}
