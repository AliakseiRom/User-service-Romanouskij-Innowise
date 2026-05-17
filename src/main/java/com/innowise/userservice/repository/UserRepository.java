package com.innowise.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.innowise.userservice.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.active = true")
    Page<User> findAllActiveUsers(Pageable pageable);

    @Query(
            value = "SELECT * FROM users WHERE active = false",
            nativeQuery = true
    )
    Page<User> findAllInactiveUsers(Pageable pageable);

    String findByEmail(String email);
}
