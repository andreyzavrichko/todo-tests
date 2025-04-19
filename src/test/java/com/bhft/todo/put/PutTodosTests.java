package com.bhft.todo.put;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static com.todo.generators.TestDataGenerator.generateTestData;

public class PutTodosTests extends BaseTest {


    @Test
    @DisplayName("TC1: Авторизованный юзер может обновить существующий TODO корректными данными.")
    public void testUpdateExistingTodoWithValidData() {
        Todo originalTodo = generateTestData(Todo.class);
        createTodo(originalTodo);

        Todo updatedTodo = new Todo(originalTodo.getId(), "Updated Task", true);

        todoRequester.getValidatedRequest().update(updatedTodo.getId(), updatedTodo);

        Optional<Todo> updated = Arrays.stream(todoRequester.getValidatedRequest().getAll())
                .filter(todo -> todo.getId() == updatedTodo.getId())
                .findFirst();

        softly.assertThat(updated)
                .as("Обновлённая задача не найдена в списке TODO")
                .isPresent();

        updated.ifPresent(todo -> {
            softly.assertThat(todo.getText()).isEqualTo(updatedTodo.getText());
            softly.assertThat(todo.isCompleted()).isEqualTo(updatedTodo.isCompleted());
        });

    }


    @Test
    @DisplayName("TC2: Попытка обновления TODO с несуществующим id.")
    public void testUpdateNonExistentTodo() {
        Todo updatedTodo = generateTestData(Todo.class);

        todoRequester.getValidatedRequest().deleteNotFound(updatedTodo.getId());
    }


    @Test
    @DisplayName("TC3: Обновление TODO с отсутствием обязательных полей.")
    public void testUpdateTodoWithMissingFields() {
        Todo originalTodo = new Todo(2, "Task to Update", false);
        createTodo(originalTodo);

        Todo invalidTodoJson = new Todo(2, true);

        todoRequester.getValidatedRequest().updateBadRequest(2, invalidTodoJson);

    }


    @Test
    @DisplayName("TC4: Обновление TODO без изменения данных (передача тех же значений).")
    public void testUpdateTodoWithoutChangingData() {
        Todo originalTodo = generateTestData(Todo.class);
        createTodo(originalTodo);

        todoRequester.getValidatedRequest().update(originalTodo.getId(), originalTodo);

        Optional<Todo> updated = Arrays.stream(todoRequester.getValidatedRequest().getAll())
                .filter(todo -> todo.getId() == originalTodo.getId())
                .findFirst();

        softly.assertThat(updated)
                .as("Задача не найдена после обновления")
                .isPresent();

        updated.ifPresent(todo -> {
            softly.assertThat(todo.getText()).isEqualTo(originalTodo.getText());
            softly.assertThat(todo.isCompleted()).isEqualTo(originalTodo.isCompleted());
        });
    }
}
