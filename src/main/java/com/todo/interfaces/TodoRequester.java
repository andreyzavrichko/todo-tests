package com.todo.interfaces;

import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import io.restassured.specification.RequestSpecification;

public class TodoRequester {
    private TodoRequest request;
    private ValidatedTodoRequest validatedRequest;

    public TodoRequester(RequestSpecification requestSpecification) {
        this.request = new TodoRequest(requestSpecification);
        this.validatedRequest = new ValidatedTodoRequest(requestSpecification);
    }

    public TodoRequest getRequest() {
        return request;
    }

    public ValidatedTodoRequest getValidatedRequest() {
        return validatedRequest;
    }
}
