package com.mohajistudio.developers.database.repository;

import com.mohajistudio.developers.database.entity.User;

public interface UserCustomRepository {
    User findByEmailAndPassword(String email, String password);
}
