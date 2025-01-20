package com.mohajistudio.developers.api.domain.post;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.PostDto;
import com.mohajistudio.developers.database.entity.MediaFile;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.repository.post.PostRepository;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import com.mohajistudio.developers.infra.service.MediaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MediaService mediaService;

    public Page<PostDto> findAllPost(Pageable pageable) {
        return postRepository.findAllPostDto(pageable, null);
    }

    public List<MediaFile> uploadImages(String email, List<MultipartFile> files) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        List<MediaFile> mediaFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            MediaFile mediaFile = mediaService.uploadImage(user.getId(), file);
            mediaFiles.add(mediaFile);
        }

        return mediaFiles;
    }
}
