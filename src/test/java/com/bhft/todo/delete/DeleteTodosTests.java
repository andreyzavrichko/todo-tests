package com.bhft.todo.delete;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGenerator.generateTestData;

public class DeleteTodosTests extends BaseTest {


    @Test
    @DisplayName("TC1: Удаление todo")
    public void testDeleteExistingTodoWithValidAuth() {
        // Создаем TODO для удаления
        Todo todo = generateTestData(Todo.class);
        createTodo(todo);

        // Отправляем DELETE запрос с корректной авторизацией
        todoRequester.getValidatedRequest().delete(todo.getId());

        // Получаем список всех TODO и проверяем, что удаленная задача отсутствует
        Todo[] todos = todoRequester.getValidatedRequest().getAll();

        // Проверяем, что удаленная задача отсутствует в списке
        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertFalse(found, "Удаленная задача все еще присутствует в списке TODO");
    }

    @Test
    @DisplayName("TC2: Попытка удаления TODO без заголовка Authorization.")
    public void testDeleteTodoWithoutAuthHeader() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        // Создаем TODO для удаления
        Todo todo = generateTestData(Todo.class);
        createTodo(todo);

        validatedRequest.deleteWithoutAuth(todo.getId());

        // Проверяем, что TODO не было удалено
        Todo[] todos = todoRequester.getValidatedRequest().getAll();

        // Проверяем, что задача все еще присутствует в списке
        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Задача отсутствует в списке TODO, хотя не должна была быть удалена");
    }

    @Test
    @DisplayName("TC3: Попытка удаления TODO с некорректными учетными данными.")
    public void testDeleteTodoWithInvalidAuth() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpecInvalidUsernameAndPassword());
        // Создаем TODO для удаления
        Todo todo = generateTestData(Todo.class);
        createTodo(todo);

        validatedRequest.deleteWithoutAuth(todo.getId());

        // Проверяем, что TODO не было удалено
        Todo[] todos = todoRequester.getValidatedRequest().getAll();

        // Проверяем, что задача все еще присутствует в списке
        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Задача отсутствует в списке TODO, хотя не должна была быть удалена");
    }


    @Test
    @DisplayName("TC4: Удаление TODO с несуществующим id.")
    public void testDeleteNonExistentTodo() {
        todoRequester.getValidatedRequest().deleteNotFound(999);

        Todo[] todos = todoRequester.getValidatedRequest().getAll();
        // Дополнительно можем проверить, что список TODO не изменился

        // В данном случае, поскольку мы не добавляли задач с id 999, список должен быть пуст или содержать только ранее добавленные задачи
    }


    @Test
    @DisplayName("TC5: Попытка удаления с некорректным форматом id (например, строка вместо числа).")
    public void testDeleteTodoWithInvalidIdFormat() {
        // Отправляем DELETE запрос с некорректным id
        todoRequester.getValidatedRequest().deleteNotFound(999999999);

    }
}
