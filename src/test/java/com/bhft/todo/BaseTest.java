package com.bhft.todo;

import com.todo.config.ConfigManager;
import com.todo.interfaces.TodoRequester;
import com.todo.models.Todo;
import com.todo.specs.request.RequestSpec;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class BaseTest {
    protected TodoRequester todoRequester;


    @BeforeAll
    public static void setup() {
        ConfigManager config = ConfigManager.getInstance();
        RestAssured.baseURI = config.get("baseUrl");
        RestAssured.port = config.getInt("port");
    }

    @BeforeEach
    public void setupTest() {
        todoRequester = new TodoRequester(RequestSpec.authSpec());
    }

    protected void createTodo(Todo todo) {
        given()
                .contentType("application/json")
                .body(todo)
                .when()
                .post("/todos")
                .then()
                .statusCode(201);
    }

    protected void deleteAllTodos() {

        Todo[] todos = given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .extract()
                .as(Todo[].class);

        for (Todo todo : todos) {
            given()
                    .auth()
                    .preemptive()
                    .basic(
                            ConfigManager.getInstance().get("username"),
                            ConfigManager.getInstance().get("password"))
                    .when()
                    .delete("/todos/" + todo.getId())
                    .then()
                    .statusCode(204);
        }
    }
}
