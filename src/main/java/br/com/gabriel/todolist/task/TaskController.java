package br.com.gabriel.todolist.task;

import br.com.gabriel.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt())|| currentDate.isAfter(taskModel.getEndAt()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início e/ou término deve ser maior que a data atual");

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor que a data de término");

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public Optional<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        return this.taskRepository.findByIdUser((UUID) idUser);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID uuid, HttpServletRequest request) {
        var task = this.taskRepository.findByIdUser(uuid).orElse(null);

        if(task == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");

        var idUser = request.getAttribute("idUser");
        if(!task.getIdUser().equals(idUser))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não possui permissão para alterar esta tarefa");

        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }
}
