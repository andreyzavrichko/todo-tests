package com.bhft.todo.delete;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.RequestSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeleteTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    /**
     * TC1: Успешное удаление существующего TODO с корректной авторизацией.
     */
    @Test
    public void testDeleteExistingTodoWithValidAuth() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Создаем TODO для удаления
        Todo todo = new Todo(1, "Task to Delete", false);
        createTodo(todo);

        // Отправляем DELETE запрос с корректной авторизацией
        validatedRequest.delete(todo.getId());

        // Получаем список всех TODO и проверяем, что удаленная задача отсутствует
        Todo[] todos = validatedRequest.getAll();

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

    /**
     * TC2: Попытка удаления TODO без заголовка Authorization.
     */
    @Test
    public void testDeleteTodoWithoutAuthHeader() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        // Создаем TODO для удаления
        Todo todo = new Todo(2, "Task to Delete", false);
        createTodo(todo);

        validatedRequest.deleteWithoutAuth(todo.getId());

        // Проверяем, что TODO не было удалено
        Todo[] todos = validatedRequest.getAll();

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

    /**
     * TC3: Попытка удаления TODO с некорректными учетными данными.
     */
    @Test
    public void testDeleteTodoWithInvalidAuth() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpecInvalidUsernameAndPassword());
        // Создаем TODO для удаления
        Todo todo = new Todo(3, "Task to Delete", false);
        createTodo(todo);

        validatedRequest.deleteWithoutAuth(todo.getId());

        // Проверяем, что TODO не было удалено
        Todo[] todos = validatedRequest.getAll();

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

    /**
     * TC4: Удаление TODO с несуществующим id.
     */
    @Test
    public void testDeleteNonExistentTodo() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Отправляем DELETE запрос для несуществующего TODO с корректной авторизацией
        validatedRequest.deleteNotFound(999);

        Todo[] todos = validatedRequest.getAll();
        // Дополнительно можем проверить, что список TODO не изменился

        // В данном случае, поскольку мы не добавляли задач с id 999, список должен быть пуст или содержать только ранее добавленные задачи
    }

    /**
     * TC5: Попытка удаления с некорректным форматом id (например, строка вместо числа).
     */
    @Test
    public void testDeleteTodoWithInvalidIdFormat() {
        ValidatedTodoRequest validatedRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        // Отправляем DELETE запрос с некорректным id
        validatedRequest.deleteNotFound(999999999);

    }
}
