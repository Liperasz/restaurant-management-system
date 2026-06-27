package com.camaradacamarao.api.repository;

import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    List<User> findAllByRole(Role role);
}
