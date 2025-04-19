package com.bhft.todo.post;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.models.TodoBuilder;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import com.todo.specs.response.IncorrectDataResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

public class PostTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    public void testCreateTodoWithValidData() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        Todo newTodo = new Todo(1, "New Task", false);

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

    /**
     * TC2: Попытка создания TODO с отсутствующими обязательными полями.
     */
    @Test
    public void testCreateTodoWithMissingFields() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Создаем JSON без обязательного поля 'text'
        Todo invalidTodoJson = new Todo(2, true);

        validatedRequest.createAndReturnBadRequest(invalidTodoJson);

    }

    /**
     * TC3: Создание TODO с максимально допустимой длиной поля 'text'.
     */
    @Test
    public void testCreateTodoWithMaxLengthText() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
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

    /**
     * TC4: Передача некорректных типов данных в полях.
     */
    @Test
    public void testCreateTodoWithInvalidDataTypes() {
        // Поле 'completed' содержит строку вместо булевого значения
        // Todo newTodo = new Todo(3, "dfsdsfdsf", false);
        Todo newTodo = new TodoBuilder().setText("dsfsfsdfdsf").build();

        todoRequester.getRequest()
                .create(newTodo)
                .then()
                .statusCode(400)
                .contentType(ContentType.TEXT)
                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
    }

    /**
     * TC5: Создание TODO с уже существующим 'id' (если 'id' задается клиентом).
     */
    @Test
    public void testCreateTodoWithExistingId() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Сначала создаем TODO с id = 5
        Todo firstTodo = new Todo(5, "First Task", false);
        createTodo(firstTodo);
        // Пытаемся создать другую TODO с тем же id
        Todo duplicateTodo = new Todo(5, "Duplicate Task", true);


        todoRequester.getRequest()
                .create(duplicateTodo)
                .then()
                .spec(new IncorrectDataResponse().sameId());

    }

}
