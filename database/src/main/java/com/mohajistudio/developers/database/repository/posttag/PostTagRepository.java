package com.mohajistudio.developers.database.repository.posttag;

import com.mohajistudio.developers.database.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long>, PostTagCustomRepository {
}
