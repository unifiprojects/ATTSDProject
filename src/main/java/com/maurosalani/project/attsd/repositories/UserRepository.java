package com.maurosalani.project.attsd.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maurosalani.project.attsd.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	User findByUsername(String string);

}

