package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUser(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/delete/{username}")  // Endpoint para eliminar un usuario por su username
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        boolean isDeleted = userService.deleteUserByEmail(username);  // Llamada al servicio para eliminar el usuario
        if (isDeleted) {
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<User> updateUser(@PathVariable String email, @RequestBody User updatedUser) {
        User user = userService.updateUser(email, updatedUser);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/assign-routine/{routineId}")
    public ResponseEntity<User> assignRoutineToUser(@PathVariable Long userId, @PathVariable Long routineId) {
        return ResponseEntity.ok(userService.assignRoutineToUser(userId, routineId));
    }

    @GetMapping("/{userId}/routine")
    public ResponseEntity<Routine> getUserRoutine(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserRoutine(userId));
    }
}
