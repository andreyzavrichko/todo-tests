package com.todo.requests;

import com.todo.models.Todo;
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

    public void create(Todo entity) {
        Response response = todoRequest.create(entity);

        response.then()
                .statusCode(SC_CREATED)
                .extract().asString();
    }


    public void delete(long id) {
        Response response = todoRequest.delete(id);

        response.then()
                .statusCode(SC_NO_CONTENT);
    }

    public void update(long id, Todo entity) {
        Response response = todoRequest.update(id, entity);

        response.then()
                .statusCode(SC_OK);

    }


    public void updateBadRequest(long id, Todo entity) {
        Response response = todoRequest.update(id, entity);

        response.then()
                .statusCode(SC_BAD_REQUEST);
    }

    public Todo[] getAll() {
        Response response = todoRequest.getAll();

        response.then()
                .statusCode(SC_OK)
                .contentType(JSON);

        return response.as(Todo[].class);
    }

    public void getAllEmpty() {
        Response response = todoRequest.getAll();

        response.then()
                .statusCode(SC_OK)
                .contentType(JSON)
                .body("", hasSize(0));

    }

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

    public void deleteWithoutAuth(long id) {
        Response response = todoRequest.delete(id);

        response.then()
                .statusCode(SC_UNAUTHORIZED);
    }

    public void deleteNotFound(long id) {
        Response response = todoRequest.delete(id);

        response.then()
                .statusCode(SC_NOT_FOUND);
    }


    public Todo[] readAll(int offset, int limit) {
        Response response = todoRequest.readAll(offset, limit);

        response.then()
                .statusCode(SC_OK)
                .contentType(JSON);

        return response.as(Todo[].class);
    }

    public Todo[] readAll(int limit) {
        Response response = todoRequest.readAll(limit);

        response.then()
                .statusCode(SC_OK)
                .contentType(JSON);

        return response.as(Todo[].class);
    }

    public void readAllBadRequest(int offset, int limit) {
        Response response = todoRequest.readAll(offset, limit);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .contentType(TEXT)
                .body(containsString("Invalid query string"));
    }

}
