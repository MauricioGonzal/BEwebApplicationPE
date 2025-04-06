package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);
    List<User> findByTrainerId(Long trainerId);
    List<User> findAllByRoleInAndIsActive(List<Role> roles, boolean isActive);
    @Query("SELECT u FROM User u WHERE (u.role IN :roles OR u.role IS NULL) AND u.isActive = :isActive")
    List<User> findAllByRoleInAndIsActiveOrRoleIsNull(@Param("roles") List<Role> roles, @Param("isActive") boolean isActive);
    @Query("SELECT DISTINCT u FROM User u JOIN Payment p ON p.user = u WHERE (u.role IN :roles) AND u.isActive = true")
    List<User> findAllForAssistance(@Param("roles") List<Role> roles);
    @Query("SELECT u FROM User u LEFT JOIN Salary s ON u = s.user WHERE u.role IN :roles AND u.isActive = true AND s.id IS NULL")
    List<User> findActiveUsersWithoutSalaries(@Param("roles") List<Role> roles);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    List<User> findAllByRoleAndIsActive(Role role, boolean isActive);
    List<User> findByIsActive(Boolean isActive);
    List<User> findByRoutine(Routine routine);

}
