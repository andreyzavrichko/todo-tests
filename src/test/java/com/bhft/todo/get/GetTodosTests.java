package com.bhft.todo.get;


import com.bhft.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.PrepareTodo;
import com.todo.models.Todo;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@Epic("TODO Management")
@Feature("Get Todos API")
@ExtendWith(DataPreparationExtension.class)
public class GetTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    @Description("Получение пустого списка TODO, когда база данных пуста")
    public void testGetTodosWhenDatabaseIsEmpty() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        validatedRequest.getAllEmpty();
    }

    @Test
    @Description("Получение списка TODO с существующими записями")
    public void testGetTodosWithExistingEntries() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Предварительно создать несколько TODO
        Todo todo1 = new Todo(1, "Task 1", false);
        Todo todo2 = new Todo(2, "Task 2", true);

        createTodo(todo1);
        createTodo(todo2);

        Todo[] todos = validatedRequest.getAll();

        // Дополнительная проверка содержимого
        Assertions.assertEquals(1, todos[0].getId());
        Assertions.assertEquals("Task 1", todos[0].getText());
        Assertions.assertFalse(todos[0].isCompleted());

        Assertions.assertEquals(2, todos[1].getId());
        Assertions.assertEquals("Task 2", todos[1].getText());
        Assertions.assertTrue(todos[1].isCompleted());
    }

    @Test
    @PrepareTodo(5)
    @Description("Использование параметров offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Создаем 5 TODO
        for (int i = 1; i <= 5; i++) {
            createTodo(new Todo(i, "Task " + i, i % 2 == 0));
        }
        Todo[] todos = validatedRequest.readAll(2, 2);

        // Проверяем, что получили задачи с id 3 и 4

        Assertions.assertEquals(3, todos[0].getId());
        Assertions.assertEquals("Task 3", todos[0].getText());

        Assertions.assertEquals(4, todos[1].getId());
        Assertions.assertEquals("Task 4", todos[1].getText());
    }

    @Test
    @DisplayName("Передача некорректных значений в offset и limit")
    public void testGetTodosWithInvalidOffsetAndLimit() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Тест с отрицательным offset
        validatedRequest.readAllBadRequest(-1, 2);

        // Тест с нечисловым limit
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", 0)
                .queryParam("limit", "abc")
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));

        // Тест с отсутствующим значением offset
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", "")
                .queryParam("limit", 2)
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));
    }

    @Test
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Создаем 10 TODO
        for (int i = 1; i <= 10; i++) {
            createTodo(new Todo(i, "Task " + i, i % 2 == 0));
        }
        Todo[] todos = validatedRequest.readAll(1000);

        // Проверяем, что вернулось 10 задач
        Assertions.assertEquals(10, todos.length);
    }
}
