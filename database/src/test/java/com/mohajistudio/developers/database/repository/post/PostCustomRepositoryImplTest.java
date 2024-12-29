package com.mohajistudio.developers.database.repository.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mohajistudio.developers.database.TestConfiguration;
import com.mohajistudio.developers.database.dto.PostDto;
import com.mohajistudio.developers.database.entity.Post;
import com.mohajistudio.developers.database.entity.PostTag;
import com.mohajistudio.developers.database.entity.Tag;
import com.mohajistudio.developers.database.enums.PostStatus;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@Transactional
@SpringBootTest
@ContextConfiguration(classes = TestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostCustomRepositoryImplTest {

    @PersistenceContext
    private EntityManager entityManager;

    private PostCustomRepositoryImpl postRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
        postRepository = new PostCustomRepositoryImpl(new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager));

        Post post = Post.builder()
                .userId(UUID.randomUUID())
                .title("Title")
                .content("Content")
                .summary("Summary")
                .status(PostStatus.PUBLISHED)
                .publishedAt(LocalDateTime.now())
                .build();
        entityManager.persist(post);

        Tag tag1 = Tag.builder()
                .userId(UUID.randomUUID())
                .title("Title")
                .slug("Slug")
                .description("Description")
                .build();

        Tag tag2 = Tag.builder()
                .userId(UUID.randomUUID())
                .title("Title")
                .slug("Slug")
                .description("Description")
                .build();

        entityManager.persist(tag1);
        entityManager.persist(tag2);

        entityManager.flush();
        entityManager.clear();

//        PostTag postTag1 = PostTag.builder().tagId(tag1.getId()).postId(post.getId()).build();
//        PostTag postTag2 = PostTag.builder().tagId(tag2.getId()).postId(post.getId()).build();
//
//        entityManager.persist(postTag1);
//        entityManager.persist(postTag2);
//
//        entityManager.flush();
//        entityManager.clear();
    }

    @Test
    @DisplayName("게시글을 페이징하여 조회")
    void testFindAllPost() {
        // Given
        Pageable pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개씩 조회

        // When
        Page<PostDto> result = postRepository.findAllPost(pageable);

        // Then
        for (PostDto post : result.getContent()) {
            try {
                String json = mapper.writeValueAsString(post);
                System.out.println(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1); // 총 게시글 수는 PUBLISHED 상태인 것만

        PostDto firstPost = result.getContent().get(0);
        assertThat(firstPost.getTitle()).isEqualTo("Title"); // 최신 게시글
    }
}
