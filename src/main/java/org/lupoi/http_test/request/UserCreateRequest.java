package org.lupoi.http_test.request;/*
    @author user
    @project HTTP_Test
    @class UserCreateRequest
    @version 1.0.0
    @since 30.04.2025 - 22.16
*/

public record UserCreateRequest(String firstName, String lastName, String number, String code, String description) {
}
