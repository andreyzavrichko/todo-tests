package com.bhft.todo.get;


import com.bhft.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.PrepareTodo;
import com.todo.models.Todo;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.todo.generators.TestDataGenerator.generateTestData;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@Epic("TODO Management")
@Feature("Get Todos API")
@ExtendWith(DataPreparationExtension.class)
public class GetTodosTests extends BaseTest {

    @Test
    @DisplayName("Получение пустого списка TODO, когда база данных пуста")
    public void testGetTodosWhenDatabaseIsEmpty() {
        todoRequester.getValidatedRequest().getAllEmpty();
    }

    @Test
    @DisplayName("Получение списка TODO с существующими записями")
    public void testGetTodosWithExistingEntries() {
        // Предварительно создать несколько TODO
        Todo todo1 = generateTestData(Todo.class);
        Todo todo2 = generateTestData(Todo.class);

        createTodo(todo1);
        createTodo(todo2);

        Todo[] todos = todoRequester.getValidatedRequest().getAll();

        softly.assertThat(todos[0].getId()).isEqualTo(todo1.getId());
        softly.assertThat(todos[0].getText()).isEqualTo(todo1.getText());
        softly.assertThat(todos[0].isCompleted()).isFalse();

        softly.assertThat(todos[1].getId()).isEqualTo(todo2.getId());
        softly.assertThat(todos[1].getText()).isEqualTo(todo2.getText());
        softly.assertThat(todos[1].isCompleted()).isFalse();

    }

    @Test
    @PrepareTodo(5)
    @DisplayName("Использование параметров offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {
        // Создаем 5 TODO
        for (int i = 1; i <= 5; i++) {
            createTodo(new Todo(i, "Task " + i, i % 2 == 0));
        }
        Todo[] todos = todoRequester.getValidatedRequest().readAll(2, 2);

        // Проверяем, что получили задачи с id 3 и 4

        // Проверяем, что получили задачи с id 3 и 4
        softly.assertThat(todos[0].getId()).isEqualTo(3);
        softly.assertThat(todos[0].getText()).isEqualTo("Task 3");

        softly.assertThat(todos[1].getId()).isEqualTo(4);
        softly.assertThat(todos[1].getText()).isEqualTo("Task 4");

    }

    @Test
    @DisplayName("Передача некорректных значений в offset и limit")
    public void testGetTodosWithInvalidOffsetAndLimit() {
        // Тест с отрицательным offset
        todoRequester.getValidatedRequest().readAllBadRequest(-1, 2);

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
    @PrepareTodo(10)
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {
        // Читаем с limit больше количества задач
        Todo[] todos = todoRequester.getValidatedRequest().readAll(1000);

        // Проверяем, что вернулось 10 задач
        Assertions.assertEquals(10, todos.length);
    }
}
