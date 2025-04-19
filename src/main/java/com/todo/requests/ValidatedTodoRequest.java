package com.todo.requests;

import com.todo.models.Todo;
import com.todo.storages.TestDataStorage;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.TEXT;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class ValidatedTodoRequest {

    private final TodoRequest todoRequest;

    public ValidatedTodoRequest(RequestSpecification reqSpec) {
        this.todoRequest = new TodoRequest(reqSpec);
    }

    @Step("Create {entity}")
    public String create(Todo entity) {
        var response = todoRequest.create(entity)
        .then()
                .statusCode(SC_CREATED)
                .extract().asString();
        TestDataStorage.getInstance().addData(entity);
        return response;
    }

    @Step("Delete {id}")
    public void delete(long id) {
        Response response = todoRequest.delete(id);

        response.then()
                .statusCode(SC_NO_CONTENT);
    }

    @Step("Update {entity}")
    public void update(long id, Todo entity) {
        Response response = todoRequest.update(id, entity);

        response.then()
                .statusCode(SC_OK);

    }

    @Step("Update {entity}")
    public void updateBadRequest(long id, Todo entity) {
        Response response = todoRequest.update(id, entity);

        response.then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Step("Get all todos")
    public Todo[] getAll() {
        Response response = todoRequest.getAll();

        response.then()
                .statusCode(SC_OK)
                .contentType(JSON);

        return response.as(Todo[].class);
    }

    @Step("Get all empty list todos")
    public void getAllEmpty() {
        Response response = todoRequest.getAll();

        response.then()
                .statusCode(SC_OK)
                .contentType(JSON)
                .body("", hasSize(0));

    }

    @Step("Create {entity}")
    public void createAndReturnBadRequest(Todo entity) {
        Response response = todoRequest.create(entity);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .contentType(TEXT)
                .body(notNullValue());
    }

    public void createAndReturnDuplicate(Todo entity) {
        Response response = todoRequest.create(entity);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body(notNullValue());
    }

    @Step("Delete without auth {id}")
    public void deleteWithoutAuth(long id) {
        Response response = todoRequest.delete(id);

        response.then()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Step("Delete not found {id}")
    public void deleteNotFound(long id) {
        Response response = todoRequest.delete(id);

        response.then()
                .statusCode(SC_NOT_FOUND);
    }

    @Step("Get all todos with {offset} and {limit}")
    public Todo[] readAll(int offset, int limit) {
        Response response = todoRequest.readAll(offset, limit);

        response.then()
                .statusCode(SC_OK)
                .contentType(JSON);

        return response.as(Todo[].class);
    }

    @Step("Get all todos with {limit}")
    public Todo[] readAll(int limit) {
        Response response = todoRequest.readAll(limit);

        response.then()
                .statusCode(SC_OK)
                .contentType(JSON);

        return response.as(Todo[].class);
    }

    @Step("Get all todos with bad request")
    public void readAllBadRequest(int offset, int limit) {
        Response response = todoRequest.readAll(offset, limit);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .contentType(TEXT)
                .body(containsString("Invalid query string"));
    }

}
