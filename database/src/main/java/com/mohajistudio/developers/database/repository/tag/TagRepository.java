package com.mohajistudio.developers.database.repository.tag;

import com.mohajistudio.developers.database.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long>, TagCustomRepository {
}
