package com.example.seleniumportal.repository;

import com.example.seleniumportal.model.PortalUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {

    Optional<PortalUser> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
