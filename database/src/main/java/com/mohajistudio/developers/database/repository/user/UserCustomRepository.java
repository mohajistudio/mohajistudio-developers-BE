package com.mohajistudio.developers.database.repository.user;

import com.mohajistudio.developers.database.entity.User;

public interface UserCustomRepository {
    User findByEmailAndPassword(String email, String password);
}
