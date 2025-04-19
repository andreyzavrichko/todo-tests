package com.bhft.todo.put;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class PutTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    /**
     * TC1: Обновление существующего TODO корректными данными.
     */
    @Test
    public void testUpdateExistingTodoWithValidData() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(1, "Original Task", false);
        createTodo(originalTodo);

        // Обновленные данные
        Todo updatedTodo = new Todo(1, "Updated Task", true);

        // Отправляем PUT запрос для обновления
        validatedRequest.update(updatedTodo.getId(), updatedTodo);

        // Проверяем, что данные были обновлены
        Todo[] todos = validatedRequest.getAll();

        Assertions.assertEquals(1, todos.length);
        Assertions.assertEquals("Updated Task", todos[0].getText());
        Assertions.assertTrue(todos[0].isCompleted());
    }

    /**
     * TC2: Попытка обновления TODO с несуществующим id.
     */
    @Test
    public void testUpdateNonExistentTodo() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Обновленные данные для несуществующего TODO
        Todo updatedTodo = new Todo(999, "Non-existent Task", true);

        validatedRequest.deleteNotFound(updatedTodo.getId());
    }

    /**
     * TC3: Обновление TODO с отсутствием обязательных полей.
     */
    @Test
    public void testUpdateTodoWithMissingFields() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(2, "Task to Update", false);
        createTodo(originalTodo);

        // Обновленные данные с отсутствующим полем 'text'
        Todo invalidTodoJson = new Todo(2, true);

        validatedRequest.updateBadRequest(2, invalidTodoJson);

    }

    /**
     * TC4: Передача некорректных типов данных при обновлении.
     */
    @Test
    public void testUpdateTodoWithInvalidDataTypes() {
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(3, "Another Task", false);
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

    /**
     * TC5: Обновление TODO без изменения данных (передача тех же значений).
     */
    @Test
    public void testUpdateTodoWithoutChangingData() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(4, "Task without Changes", false);
        createTodo(originalTodo);
        validatedRequest.update(4, originalTodo);

        Todo[] todo = validatedRequest.getAll();
        // Проверяем, что данные не изменились

        Assertions.assertEquals("Task without Changes", todo[0].getText());
        Assertions.assertFalse(todo[0].isCompleted());
    }
}
