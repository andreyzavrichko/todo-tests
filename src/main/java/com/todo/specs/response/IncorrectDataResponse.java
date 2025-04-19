package com.todo.specs.response;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

public class IncorrectDataResponse {
    public ResponseSpecification sameId() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(SC_BAD_REQUEST);
        responseSpecBuilder.expectBody(Matchers.containsString("You are trying to use the same id"));
        return responseSpecBuilder.build();
    }
}
