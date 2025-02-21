package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
    List<User> findByTrainerIdAndRole(Long trainerId, Role role);
    List<User> findAllByRole(Role role);
    List<User> findAllByRoleInAndIsActive(List<Role> roles, boolean isActive);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    List<User> findAllByRoleAndIsActive(Role role, boolean isActive);

}
