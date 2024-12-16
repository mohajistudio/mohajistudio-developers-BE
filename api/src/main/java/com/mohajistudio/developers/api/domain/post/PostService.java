package com.mohajistudio.developers.api.domain.post;

import com.mohajistudio.developers.database.dto.PostDto;
import com.mohajistudio.developers.database.repository.post.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Page<PostDto> findAllPost(Pageable pageable) {
        return postRepository.findAllPost(pageable);
    }
}
