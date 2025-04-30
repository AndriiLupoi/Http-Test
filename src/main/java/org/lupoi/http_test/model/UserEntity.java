package org.lupoi.http_test.model;/*
    @author user
    @project HTTP_Test
    @class User
    @version 1.0.0
    @since 10.04.2025 - 12.58
*/

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document
public class UserEntity {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String number;
    private String description;
    //---------- custom audit  ----------------
    private LocalDateTime createDate;
    private List<LocalDateTime> updateDate;

    public UserEntity(String firstName, String lastName, String number, String description) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity user = (UserEntity) o;
        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
