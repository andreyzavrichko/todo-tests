package com.todo.specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.preemptive;

public class RequestSpec {

    private static RequestSpecBuilder baseSpecBuilder() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.addFilters(List.of(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()));
        requestSpecBuilder.setContentType(ContentType.JSON);
        requestSpecBuilder.setAccept(ContentType.JSON);
        return requestSpecBuilder;
    }

    public static RequestSpecification unauthSpec() {
        return baseSpecBuilder().build();
    }

    public static RequestSpecification authSpec() {
        RequestSpecBuilder builder = baseSpecBuilder();

        builder.setAuth(preemptive().basic("admin", "admin"));

        return builder.build();
    }

    public static RequestSpecification authSpecInvalidUsernameAndPassword() {
        RequestSpecBuilder builder = baseSpecBuilder();

        builder.setAuth(preemptive().basic("invalidUser", "invalidPass"));

        return builder.build();
    }
}
