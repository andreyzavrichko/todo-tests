package com.todo.requests;

import com.todo.interfaces.CrudInterface;
import com.todo.interfaces.SearchInterface;
import com.todo.models.Todo;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class TodoRequest extends Request implements CrudInterface<Todo>, SearchInterface<Todo> {

    private static final String TODO_ENDPOINT = "/todos";

    public TodoRequest(RequestSpecification reqSpec) {
        super(reqSpec);
    }

    @Override
    @Step("Create {entity}")
    public Response create(Todo entity) {
        return given()
                .spec(reqSpec)
                .body(entity)
                .when()
                .post(TODO_ENDPOINT);
    }

    @Override
    @Step("Update {entity}")
    public Response update(long id, Todo entity) {
        return given()
                .spec(reqSpec)
                .body(entity)
                .when()
                .put(TODO_ENDPOINT + "/" + id);
    }

    @Override
    @Step("Delete {id}")
    public Response delete(long id) {
        return given()
                .spec(reqSpec)
                .when()
                .delete(TODO_ENDPOINT + "/" + id);
    }

    @Step("Get all todos")
    public Response getAll() {
        return given()
                .spec(reqSpec)
                .when()
                .get(TODO_ENDPOINT);
    }

    @Override
    @Step("Get all todos")
    public Response readAll() {
        return given()
                .spec(reqSpec)
                .when()
                .get(TODO_ENDPOINT);
    }

    @Override
    @Step("Get all todos with {offset} and {limit}")
    public Response readAll(int offset, int limit) {
        return given()
                .spec(reqSpec)
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .when()
                .get(TODO_ENDPOINT);
    }

    @Step("Get all todos with {limit}")
    public Response readAll(int limit) {
        return given()
                .spec(reqSpec)
                .queryParam("limit", limit)
                .when()
                .get(TODO_ENDPOINT);
    }
}
