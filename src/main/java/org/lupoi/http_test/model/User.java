package org.lupoi.http_test.model;/*
    @author user
    @project HTTP_Test
    @class User
    @version 1.0.0
    @since 10.04.2025 - 12.58
*/

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String number;

    public User(String id, String firstName, String lastName, String number) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
    }
}
