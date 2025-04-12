package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AssignRoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineSetResponse;
import com.aplicaciongimnasio.PuraEsencia.dto.UserRequest;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineSetRepository routineSetRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipItemRepository membershipItemRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoutineSetSeriesRepository routineSetSeriesRepository;

    public User createUser(User user) {
        if(userRepository.findByEmailAndIsActive(user.getEmail(), true).isPresent()){
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        String encryptedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encryptedPassword);

        user.setIsActive(true);



        return userRepository.save(user);
    }

    public User createUserByAdmin(UserRequest userRequest) {
        if(userRepository.findByEmailAndIsActive(userRequest.getEmail(), true).isPresent()){
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        User admin = userRepository.findById(userRequest.getAdminId()).orElseThrow(()-> new RuntimeException("ERROR. Contactar con soporte."));

        String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());

        User user = new User();
        user.setPassword(encryptedPassword);
        user.setEmail(userRequest.getEmail());
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf(userRequest.getRole()));
        user.setGym(admin.getGym());

        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndIsActive(email, true);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean logicDelete(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userOptional.get().setIsActive(false);
            userRepository.save(userOptional.get());
            return true;
        }
        return false;
    }

    public User updateUser(String email, User updatedUser) {
        User user = userRepository.findByEmailAndIsActive(email, true)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (updatedUser.getRoutine() != null) {
            user.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }
        return userRepository.save(user);
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public User assignRoutineToUser(AssignRoutineRequest assignRoutineRequest) {
        User user = userRepository.findById(assignRoutineRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Routine routine = routineRepository.findById(assignRoutineRequest.getRoutineId())
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        User trainer = userRepository.findById(assignRoutineRequest.getTrainerId())
                .orElseThrow(() -> new RuntimeException("Trainer no encontrado"));

        user.setRoutine(routine);
        user.setTrainer(trainer);
        return userRepository.save(user);
    }

    public List<RoutineSetResponse> getUserRoutine(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<RoutineSet> routineSets = routineSetRepository.findByRoutineAndIsActive(user.getRoutine(), true);

        List<RoutineSetResponse> responses = routineSets.stream()
                .map(rs -> {
                    List<Byte> series = routineSetSeriesRepository.getRepetitionsByRoutineSet(rs);
                    return new RoutineSetResponse(
                            rs.getRoutine(),
                            rs.getDayNumber(),
                            rs.getExerciseIds(),
                            rs.getSeries(),
                            rs.getRest(),
                            series
                    );
                }).collect(Collectors.toList());

        return responses;
    }

    public void assignClientToTrainer(Long clientId, Long trainerId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

        if (!trainer.getRole().equals(Role.TRAINER)) {
            throw new IllegalArgumentException("El usuario seleccionado no es un entrenador");
        }

        client.setTrainer(trainer);
        userRepository.save(client);
    }

    public List<User> getClientsByTrainerId(Long trainerId) {
        return userRepository.findByTrainerId(trainerId);
    }

    public List<User> getAllByRole(String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            return userRepository.findAllByRoleAndIsActive(userRole, true);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role no válido: " + role);
        }
    }

    public List<User> getAllForAssistance() {
        List<Role> roles = Stream.of("CLIENT")
                    .map(Role::valueOf)
                    .collect(Collectors.toList());

        return userRepository.findAllForAssistance(roles);
    }



    public List<User> getAll() {
        return userRepository.findByIsActiveAndRoleNot(true, Role.SUPER_ADMIN);

    }

    public List<User> getAllForSalary() {
            List<Role> roles = Stream.of("TRAINER", "RECEPTIONIST")
                    .map(Role::valueOf)
                    .collect(Collectors.toList());

            return userRepository.findActiveUsersWithoutSalaries(roles);
    }

    public Optional<User> getTrainerByOneClient(Long clientId){
        Optional<User> user = userRepository.findById(clientId);
        return Optional.ofNullable(user.get().getTrainer());
    }

    public Boolean assignHealthRecord(Long userId, HealthRecord HealthRecord){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setHealthRecord(HealthRecord);

        userRepository.save(user);

        return true;
    }

    public List<User> getAllGymUsers() {
        List<Payment> paymentList = paymentRepository.findActualPayment(LocalDate.now());
        List<User> users = new ArrayList<>();

        Area area = areaRepository.findByName("Musculacion");
        if(area == null) throw new RuntimeException("ERROR. Contactar a soporte");

        for(Payment payment: paymentList){
            List<MembershipItem> associatedMemberships = membershipItemRepository.findByMembershipPrincipal(payment.getMembership());
            if(!associatedMemberships.isEmpty()){
                associatedMemberships.stream()
                        .map(MembershipItem::getMembershipAssociated)
                        .filter(Objects::nonNull) // Por si acaso hay alguno null
                        .forEach(associated -> {
                          if(associated.getArea() == area){
                              users.add(payment.getUser());
                              return;
                          }
                        }
                        );
            }
            else{
                if(payment.getMembership().getArea() == area){
                    users.add(payment.getUser());
                }
            }
        }
        return users;
    }

    public List<User> getAllClassesUsers() {
        List<Payment> paymentList = paymentRepository.findActualPayment(LocalDate.now());
        List<User> users = new ArrayList<>();

        Area area = areaRepository.findByName("Clases");
        if(area == null) throw new RuntimeException("ERROR. Contactar a soporte");

        for(Payment payment: paymentList){
            List<MembershipItem> associatedMemberships = membershipItemRepository.findByMembershipPrincipal(payment.getMembership());
            if(!associatedMemberships.isEmpty()){
                associatedMemberships.stream()
                        .map(MembershipItem::getMembershipAssociated)
                        .filter(Objects::nonNull) // Por si acaso hay alguno null
                        .forEach(associated -> {
                                    if(associated.getArea() == area){
                                        users.add(payment.getUser());
                                        return;
                                    }
                                }
                        );
            }
            else{
                if(payment.getMembership().getArea() == area){
                    users.add(payment.getUser());
                }
            }
        }
        return users;
    }



}
