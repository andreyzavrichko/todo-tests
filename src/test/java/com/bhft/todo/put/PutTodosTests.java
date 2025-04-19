package com.bhft.todo.put;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGenerator.generateTestData;
import static io.restassured.RestAssured.given;

public class PutTodosTests extends BaseTest {


    @Test
    @DisplayName("TC1: Обновление существующего TODO корректными данными.")
    public void testUpdateExistingTodoWithValidData() {
        // Создаем TODO для обновления
        Todo originalTodo = generateTestData(Todo.class);
        createTodo(originalTodo);

        // Обновленные данные
        Todo updatedTodo = new Todo(originalTodo.getId(), "Updated Task", true);

        // Отправляем PUT запрос для обновления
        todoRequester.getValidatedRequest().update(updatedTodo.getId(), updatedTodo);

        // Проверяем, что данные были обновлены
        Todo[] todos = todoRequester.getValidatedRequest().getAll();

        softly.assertThat(todos.length).isEqualTo(1);
        softly.assertThat(todos[0].getText()).isEqualTo("Updated Task");
        softly.assertThat(todos[0].isCompleted()).isTrue();

    }


    @Test
    @DisplayName("TC2: Попытка обновления TODO с несуществующим id.")
    public void testUpdateNonExistentTodo() {
        // Обновленные данные для несуществующего TODO
        Todo updatedTodo = generateTestData(Todo.class);

        todoRequester.getValidatedRequest().deleteNotFound(updatedTodo.getId());
    }


    @Test
    @DisplayName("TC3: Обновление TODO с отсутствием обязательных полей.")
    public void testUpdateTodoWithMissingFields() {
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(2, "Task to Update", false);
        createTodo(originalTodo);

        // Обновленные данные с отсутствующим полем 'text'
        Todo invalidTodoJson = new Todo(2, true);

        todoRequester.getValidatedRequest().updateBadRequest(2, invalidTodoJson);

    }


    @Test
    @DisplayName("TC4: Передача некорректных типов данных при обновлении.")
    public void testUpdateTodoWithInvalidDataTypes() {
        // Создаем TODO для обновления
        Todo originalTodo = generateTestData(Todo.class);
        createTodo(originalTodo);

        // Обновленные данные с некорректным типом поля 'completed'
        String invalidTodoJson = "{ \"id\": 3, \"text\": \"Updated Task\", \"completed\": \"notBoolean\" }";

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(invalidTodoJson)
                .when()
                .put("/todos/3")
                .then()
                .statusCode(401);
    }


    @Test
    @DisplayName("TC5: Обновление TODO без изменения данных (передача тех же значений).")
    public void testUpdateTodoWithoutChangingData() {
        // Создаем TODO для обновления
        Todo originalTodo = generateTestData(Todo.class);
        createTodo(originalTodo);
        todoRequester.getValidatedRequest().update(originalTodo.getId(), originalTodo);

        Todo[] todo = todoRequester.getValidatedRequest().getAll();
        // Проверяем, что данные не изменились

        softly.assertThat(todo[0].getText()).isEqualTo(originalTodo.getText());
        softly.assertThat(todo[0].isCompleted()).isFalse();
    }
}
