package com.mohajistudio.developers.database.repository.tag;

import com.mohajistudio.developers.database.entity.Tag;

public interface TagCustomRepository {
    Tag findByTitle(String title);
}