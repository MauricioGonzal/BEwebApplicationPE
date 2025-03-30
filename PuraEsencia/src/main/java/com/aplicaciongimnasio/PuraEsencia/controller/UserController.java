package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.AssignRoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.ChangePasswordRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.UserRequest;
import com.aplicaciongimnasio.PuraEsencia.model.RoutineSet;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/byAdmin")
    public ResponseEntity<?> createUserByAdmin(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.createUserByAdmin(userRequest));
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> getAll(){
        return userService.getAll();
    }

    @GetMapping("/getAllByRole/{role}")
    public List<User> getAllByRole(@PathVariable String role) {
        return userService.getAllByRole(role);
    }

    @GetMapping("/getAllForAssistance")
    public List<User> getAllForAssistance() {
        return userService.getAllForAssistance();
    }

    @GetMapping("/getAllGymUsers")
    public List<User> getAllGymUsers() {
        return userService.getAllGymUsers();
    }

    @GetMapping("/getForSalary")
    public List<User> getAllForSalary() {
        return userService.getAllForSalary();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        boolean isDeleted = userService.logicDelete(userId);
        if (isDeleted) {
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<User> updateUser(@PathVariable String email, @RequestBody User updatedUser) {
        User user = userService.updateUser(email, updatedUser);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        boolean isUpdated = userService.changePassword(
                request.getUserId(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        if (!isUpdated) {
            return ResponseEntity.badRequest().body("Error: Contraseña actual incorrecta");
        }
        return ResponseEntity.ok("Contraseña cambiada exitosamente");
    }


    @PutMapping("/assign-routine")
    public ResponseEntity<User> assignRoutineToUser(@RequestBody AssignRoutineRequest assignRoutineRequest) {
        var response =ResponseEntity.ok(userService.assignRoutineToUser(assignRoutineRequest));

        // Enviar el mensaje al topic del usuario con el ID
        String userTopic = "/topic/assign-routine/" + assignRoutineRequest.getUserId();
        messagingTemplate.convertAndSend(userTopic, response);

        return response;
    }

    @GetMapping("/{userId}/routine")
    public ResponseEntity<List<RoutineSet>> getUserRoutine(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserRoutine(userId));
    }

    @PostMapping("/assign-trainer")
    public void assignClientToTrainer(@RequestParam Long clientId, @RequestParam Long trainerId) {
        userService.assignClientToTrainer(clientId, trainerId);
    }

    @GetMapping("/{trainerId}/clients")
    public List<User> getClientsByTrainer(@PathVariable Long trainerId) {
        return userService.getClientsByTrainerId(trainerId);
    }

    @GetMapping("/{clientId}/trainer")
    public Optional<User> getTrainerByOneClient(@PathVariable Long clientId) {
        return userService.getTrainerByOneClient(clientId);
    }


}
