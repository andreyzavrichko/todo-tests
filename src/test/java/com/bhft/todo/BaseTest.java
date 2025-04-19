package com.bhft.todo;

import com.todo.config.ConfigManager;
import com.todo.interfaces.TodoRequester;
import com.todo.models.Todo;
import com.todo.requests.TodoRequest;
import com.todo.specs.request.RequestSpec;
import com.todo.storages.TestDataStorage;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class BaseTest {
    protected TodoRequester todoRequester;
    protected SoftAssertions softly;

    @BeforeAll
    public static void setup() {
        ConfigManager config = ConfigManager.getInstance();
        RestAssured.baseURI = config.get("baseUrl");
        RestAssured.port = config.getInt("port");
    }

    @BeforeEach
    public void setupTest() {
       // deleteAllTodos();
        todoRequester = new TodoRequester(RequestSpec.authSpec());
        softly = new SoftAssertions();

    }

    public void createTodo(Todo todo) {
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

    @AfterEach
    public void assertSoftly() {
        softly.assertAll();
    }

    @AfterEach
    public void clean() {
        TestDataStorage.getInstance().getStorage()
                .forEach((k, v) ->
                        new TodoRequest(RequestSpec.authSpec())
                                .delete(k));

        TestDataStorage.getInstance().clean();
    }
}
