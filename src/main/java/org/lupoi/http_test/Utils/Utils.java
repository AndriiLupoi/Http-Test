package org.lupoi.http_test.Utils;/*
    @author user
    @project HTTP_Test
    @class Utils
    @version 1.0.0
    @since 07.05.2025 - 12.09
*/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    public static String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

}