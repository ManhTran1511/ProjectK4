package com.fpt.edu.repository;

import java.util.Optional;

import com.fpt.edu.models.ERole;
import com.fpt.edu.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
