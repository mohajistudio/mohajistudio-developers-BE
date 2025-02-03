package com.mohajistudio.developers.api.domain.post;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.PostDetailsDto;
import com.mohajistudio.developers.database.dto.PostDto;
import com.mohajistudio.developers.database.entity.*;
import com.mohajistudio.developers.database.enums.PostStatus;
import com.mohajistudio.developers.database.repository.mediafile.MediaFileRepository;
import com.mohajistudio.developers.database.repository.post.PostRepository;
import com.mohajistudio.developers.database.repository.posttag.PostTagRepository;
import com.mohajistudio.developers.database.repository.tag.TagRepository;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import com.mohajistudio.developers.database.utils.RedisUtil;
import com.mohajistudio.developers.infra.service.MediaService;
import com.mohajistudio.developers.infra.util.MediaUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final RedisUtil redisUtil;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MediaService mediaService;
    private final TagRepository tagRepository;
    private final MediaFileRepository mediaFileRepository;
    private final PostTagRepository postTagRepository;

    public Page<PostDto> findAllPost(Pageable pageable) {
        return postRepository.findAllPostDto(pageable, null);
    }

    public List<MediaFile> uploadMediaFiles(String email, List<MultipartFile> files) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        List<MediaFile> mediaFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            MediaFile mediaFile = mediaService.uploadMediaFileToTempFolder(user.getId(), file);
            mediaFiles.add(mediaFile);
        }

        return mediaFiles;
    }

    public Post publishPost(UUID userId, String title, String summary, String content, UUID thumbnailId, PostStatus status, List<String> tags) {
        LocalDateTime publishedAt = LocalDateTime.now();

        Post post = Post.builder().userId(userId).title(title).summary(summary).content(content).publishedAt(publishedAt).status(status).build();

        if (thumbnailId != null) {
            MediaFile findMediaFile = mediaService.findByIdAndUserId(thumbnailId, userId);

            if (findMediaFile == null) {
                throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "유효하지 않은 썸네일");
            }

            post.setThumbnail(findMediaFile.getFileName());
            post.setThumbnailId(thumbnailId);
        }

        Post savedPost = postRepository.save(post);

        for (String tag : tags) {
            Tag findTag = tagRepository.findByTitle(tag);

            if (findTag == null) {
                Tag newTag = Tag.builder().title(tag).userId(userId).build();
                Tag savedTag = tagRepository.save(newTag);

                PostTag postTag = PostTag.builder().postId(savedPost.getId()).tagId(savedTag.getId()).build();
                postTagRepository.save(postTag);
            } else {
                PostTag postTag = PostTag.builder().postId(savedPost.getId()).tagId(findTag.getId()).build();
                postTagRepository.save(postTag);
            }
        }

        return savedPost;
    }

    public String processHtmlImagesForPermanentStorage(UUID userId, String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        Elements imgTags = document.select("img[src]");
        String awsS3BaseUrl = MediaUtil.getAwsS3BaseUrl();

        for (Element img : imgTags) {
            String src = img.attr("src");
            if (src.startsWith(awsS3BaseUrl + "/media/temp")) {
                String extractedMediaFileId = MediaUtil.extractIdFromFileName(src);

                UUID mediaFileId = UUID.fromString(extractedMediaFileId);

                MediaFile findMediaFile = mediaFileRepository.findByIdAndUserId(mediaFileId, userId);
                if (findMediaFile == null) {
                    throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "알 수 없는 미디어 파일");
                }

                MediaFile savedMediaFile = mediaService.moveToPermanentFolder(findMediaFile);

                img.attr("src", awsS3BaseUrl + savedMediaFile.getFileName());
            }
        }

        return document.body().html();
    }

    public PostDetailsDto findPost(UUID postId) {
        PostDetailsDto findPostDetailsDto = postRepository.findByIdPostDetailsDto(postId);

        if (findPostDetailsDto == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "알 수 없는 게시글");
        }

        return findPostDetailsDto;
    }

    public void increaseViewCount(UUID postId, UUID userId, String ipAddress) {
        String redisKey;

        if (userId != null) {
            redisKey = "post:view:" + postId + ":user:" + userId;
        } else {
            redisKey = "post:view:" + postId + ":ip:" + ipAddress;
        }

        if (!redisUtil.hasKey(redisKey)) {
            postRepository.incrementViewCount(postId);

            redisUtil.setValue(redisKey, "1", 10, TimeUnit.MINUTES);
        }
    }
}
