package com.mohajistudio.developers.api.domain.post;

import com.mohajistudio.developers.api.domain.tag.TagService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final RedisUtil redisUtil;
    private final TagService tagService;
    private final MediaService mediaService;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final MediaFileRepository mediaFileRepository;

    private static final String POST_VIEW_PREFIX = "post:view:";


    public Page<PostDto> findAllPost(Pageable pageable, UUID userId, String search, List<String> tags, PostStatus status) {
        return postRepository.findAllPostDto(pageable, userId, search, tags, status);
    }

    public UUID publishPost(UUID userId, String title, String summary, String content, UUID thumbnailId, LocalDateTime publishedAt, PostStatus status, List<String> tags) {
        Post post = Post.builder().userId(userId).title(title).summary(summary).content(content).publishedAt(publishedAt).status(status).build();

        if (thumbnailId != null) {
            MediaFile findMediaFile = mediaFileRepository.findByIdAndUserId(thumbnailId, userId);

            if (findMediaFile == null) {
                throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "유효하지 않은 썸네일");
            }

            post.setThumbnail(findMediaFile.getFileName());
            post.setThumbnailId(thumbnailId);
        }

        Post savedPost = postRepository.save(post);

        for (String tag : tags) {
            tagService.addTag(tag, userId, post.getId());
        }

        return savedPost.getId();
    }

    public void deletePostTagAndDecreaseTagCount(UUID postId, UUID tagId) {
        tagRepository.decrementTagCount(tagId);

        postTagRepository.deleteByTagIdAndPostId(tagId, postId);
    }

    public void deletePost(UUID postId) {
        postRepository.deleteById(postId);
    }

    public String processHtmlImagesForPermanentStorage(UUID userId, String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        Elements imgTags = document.select("img[src]");
        String awsS3BaseUrl = MediaUtil.getAwsS3BaseUrl();
        document.outputSettings().prettyPrint(false);

        for (Element img : imgTags) {
            String src = img.attr("src");
            if (src.startsWith(awsS3BaseUrl + "/media/temp")) {
                String extractedMediaFileId = MediaUtil.extractIdFromFileName(src);

                UUID mediaFileId = UUID.fromString(extractedMediaFileId);

                MediaFile findMediaFile = mediaFileRepository.findByIdAndUserId(mediaFileId, userId);
                if (findMediaFile == null) {
                    throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "알 수 없는 미디어 파일");
                }

                MediaFile mediaFile = mediaService.moveToPermanentFolder(findMediaFile);

                MediaFile savedMediaFile = mediaFileRepository.save(mediaFile);

                img.attr("src", awsS3BaseUrl + savedMediaFile.getFileName());
            }
        }

        return document.body().html();
    }

    public void deleteMediaFilesInHtml(UUID userId, String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        Elements imgTags = document.select("img[src]");
        String awsS3BaseUrl = MediaUtil.getAwsS3BaseUrl();
        document.outputSettings().prettyPrint(false);  // 개행 유지

        for (Element img : imgTags) {
            String src = img.attr("src");

            if (src.startsWith(awsS3BaseUrl + "/media/images") || src.startsWith(awsS3BaseUrl + "/media/videos")) {
                String extractedMediaFileId = MediaUtil.extractIdFromFileName(src);

                try {
                    UUID mediaFileId = UUID.fromString(extractedMediaFileId);

                    MediaFile mediaFile = mediaFileRepository.findByIdAndUserId(mediaFileId, userId);

                    if (mediaFile == null) {
                        throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "알 수 없는 미디어 파일");
                    }

                    mediaService.delete(mediaFile);

                    mediaFileRepository.delete(mediaFile);

                } catch (IllegalArgumentException e) {
                    throw new CustomException(ErrorCode.INVALID_MEDIA_FILE);
                }
            }
        }
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
            redisKey = POST_VIEW_PREFIX + postId + ":user:" + userId;
        } else {
            redisKey = POST_VIEW_PREFIX + postId + ":ip:" + ipAddress;
        }

        if (!redisUtil.hasKey(redisKey)) {
            postRepository.incrementViewCount(postId);

            redisUtil.setValue(redisKey, "1", 10, TimeUnit.MINUTES);
        }
    }
}
