package org.lupoi.http_test.request;/*
    @author user
    @project HTTP_Test
    @class UserUpdateRequest
    @version 1.0.0
    @since 30.04.2025 - 22.16
*/

public record UserUpdateRequest (String id, String firstName, String lastName, String number, String description){
}
