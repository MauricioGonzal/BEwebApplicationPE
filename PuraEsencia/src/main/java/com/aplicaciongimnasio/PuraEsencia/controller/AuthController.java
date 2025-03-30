package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.LoginRequest;
import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import com.aplicaciongimnasio.PuraEsencia.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmailAndIsActive(loginRequest.getEmail(), true);
        if (user.isEmpty()) throw new RuntimeException("Usuario o contraseña incorrectos.");

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("Usuario o contraseña incorrectos.");
        }

        EnumSet<Role> excludedRoles = EnumSet.of(Role.SUPER_ADMIN, Role.ADMIN, Role.TRAINER, Role.RECEPTIONIST);
        if (!excludedRoles.contains(user.get().getRole())) {
            List<Payment> payments = paymentRepository.findLatestActivePaymentsByUser(LocalDate.now(), user.get().getId());
            if (payments.isEmpty()) throw new RuntimeException("Ingreso Inválido.");
            else if(payments.getFirst().getMembership() != null){
                user.get().setRole(payments.getFirst().getMembership().getTransactionCategory().getRoleAccepted());
                userRepository.save(user.get());
            }
        }

        return jwtUtil.generateToken(authentication.getName(), user.get().getRole(), user.get().getId());
    }

}