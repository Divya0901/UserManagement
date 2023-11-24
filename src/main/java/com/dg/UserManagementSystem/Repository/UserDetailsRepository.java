package com.dg.UserManagementSystem.Repository;

import com.dg.UserManagementSystem.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

}

