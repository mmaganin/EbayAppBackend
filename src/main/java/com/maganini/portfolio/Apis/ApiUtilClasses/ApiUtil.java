package com.maganini.portfolio.Apis.ApiUtilClasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiUtil {
    public static Object mapStrResponseToObj(String jsonResponse, Class classType) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Object output;
        output = om.readValue(jsonResponse, classType);
        return output;
    }

    public static Map<String, Object> mapStrResponseToMap(String jsonResponse) throws JsonProcessingException {
        Map<String, Object> jsonMap;
        ObjectMapper om = new ObjectMapper();
        jsonMap = om.readValue(jsonResponse, new TypeReference<>() {}); // converts JSON to Map
        return jsonMap;
    }
}