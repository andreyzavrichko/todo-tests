package com.bhft.todo.post;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.models.TodoBuilder;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import com.todo.specs.response.IncorrectDataResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGenerator.generateTestData;
import static org.hamcrest.Matchers.notNullValue;

public class PostTodosTests extends BaseTest {


    @Test
    @DisplayName("TC1: Создание TODO с валидными данными")
    public void testCreateTodoWithValidData() {
        Todo newTodo = generateTestData(Todo.class);

        // Создаём TODO и получаем его обратно
        todoRequester.getValidatedRequest().create(newTodo);

        // Получаем список и убеждаемся, что созданный TODO присутствует
        Todo[] todos = todoRequester.getValidatedRequest().getAll();
        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                Assertions.assertEquals(newTodo.getText(), todo.getText());
                Assertions.assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Созданная задача не найдена в списке TODO");
    }


    @Test
    @DisplayName("TC2: Попытка создания TODO с отсутствующими обязательными полями.")
    public void testCreateTodoWithMissingFields() {
        // Создаем JSON без обязательного поля 'text'
        Todo invalidTodoJson = new Todo(2, true);
        todoRequester.getValidatedRequest()
                .createAndReturnBadRequest(invalidTodoJson);

    }


    @Test
    @DisplayName("TC3: Создание TODO с максимально допустимой длиной поля 'text'.")
    public void testCreateTodoWithMaxLengthText() {
        // Предполагаем, что максимальная длина поля 'text' составляет 255 символов
        String maxLengthText = "A".repeat(255);
        Todo newTodo = new Todo(3, maxLengthText, false);

        // Отправляем POST запрос для создания нового TODO
        todoRequester.getValidatedRequest()
                .create(newTodo);

        // Проверяем, что TODO было успешно создано
        Todo[] todos = todoRequester.getValidatedRequest().getAll();

        // Ищем созданную задачу в списке
        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                Assertions.assertEquals(newTodo.getText(), todo.getText());
                Assertions.assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Созданная задача не найдена в списке TODO");
    }


    @Test
    @DisplayName("TC4: Передача некорректных типов данных в полях.")

    public void testCreateTodoWithInvalidDataTypes() {
        // Поле 'completed' содержит строку вместо булевого значения
        Todo newTodo = new TodoBuilder().setText("dsfsfsdfdsf").build();

        todoRequester.getRequest()
                .create(newTodo)
                .then()
                .statusCode(400)
                .contentType(ContentType.TEXT)
                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
    }


    @Test
    @DisplayName("TC5: Создание TODO с уже существующим 'id' (если 'id' задается клиентом).")
    public void testCreateTodoWithExistingId() {
        Todo firstTodo = generateTestData(Todo.class);
        createTodo(firstTodo);
        // Пытаемся создать другую TODO с тем же id
        Todo duplicateTodo = new Todo(firstTodo.getId(), "Duplicate Task", true);

        todoRequester.getRequest()
                .create(duplicateTodo)
                .then()
                .spec(new IncorrectDataResponse().sameId());

    }

}
