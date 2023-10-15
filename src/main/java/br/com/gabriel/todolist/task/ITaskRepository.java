package br.com.gabriel.todolist.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    Optional<TaskModel> findByIdUser(UUID idUser);
//    TaskModel findByIdAndByIdUser(UUID id, UUID idUser);
}
