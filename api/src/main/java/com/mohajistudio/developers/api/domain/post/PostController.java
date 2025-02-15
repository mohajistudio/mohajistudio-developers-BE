package com.mohajistudio.developers.api.domain.post;

import com.mohajistudio.developers.api.domain.post.dto.request.CreateAndUpdatePostRequest;
import com.mohajistudio.developers.api.domain.post.dto.request.GenerateSummaryRequest;
import com.mohajistudio.developers.api.domain.post.dto.request.GetPostRequest;
import com.mohajistudio.developers.authentication.dto.CustomUserDetails;
import com.mohajistudio.developers.common.dto.response.CustomPageResponse;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.common.utils.HttpUtil;
import com.mohajistudio.developers.database.dto.PostDetailsDto;
import com.mohajistudio.developers.database.dto.PostDto;
import com.mohajistudio.developers.database.enums.PostStatus;
import com.mohajistudio.developers.database.repository.post.PostRepository;
import com.mohajistudio.developers.infra.service.AzureOpenAiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final AzureOpenAiService azureOpenAiService;
    private final PostRepository postRepository;

    @GetMapping
    CustomPageResponse<PostDto> getPosts(Pageable pageable, GetPostRequest getPostRequest) {
        Page<PostDto> posts = postService.findAllPost(pageable, getPostRequest.getUserId(), getPostRequest.getSearch(), getPostRequest.getTags(), PostStatus.PUBLISHED);

        return new CustomPageResponse<>(posts);
    }

    @PostMapping
    UUID addPost(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CreateAndUpdatePostRequest createPostRequest) {
        String updatedHtmlContent = postService.processHtmlImagesForPermanentStorage(userDetails.getUserId(), createPostRequest.getContent());

        createPostRequest.setContent(updatedHtmlContent);

        LocalDateTime publishedAt = null;

        if (createPostRequest.getStatus() == PostStatus.PUBLISHED) {
            publishedAt = LocalDateTime.now();
        }

        return postService.publishPost(userDetails.getUserId(), createPostRequest.getTitle(), createPostRequest.getSummary(), createPostRequest.getContent(), createPostRequest.getThumbnailId(), publishedAt, createPostRequest.getStatus(), createPostRequest.getTags());
    }

    @GetMapping("/{postId}")
    PostDetailsDto getPost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID postId, HttpServletRequest request) throws UnknownHostException {
            UUID userId = null;
            if(userDetails != null) {
                userId = userDetails.getUserId();
            }

            String ipAddress = HttpUtil.getClientIp(request);

            postService.increaseViewCount(postId, userId, ipAddress);

            return postService.findPost(postId);
    }

    @PostMapping(value = "/generate-metadata")
    String postGenerateMetadata(@RequestBody GenerateSummaryRequest generateSummaryRequest) {
        return azureOpenAiService.generatePostMetadata(generateSummaryRequest.getContent());
    }

    @PatchMapping("/{postId}")
    UUID updatePost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID postId, @RequestBody CreateAndUpdatePostRequest updatePostRequest) {
        PostDetailsDto post = postRepository.findByIdPostDetailsDto(postId);

        if(post == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "알 수 없는 게시글");
        }

        if(!post.getUser().getId().equals(userDetails.getUserId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String updatedHtmlContent = postService.processHtmlImagesForPermanentStorage(userDetails.getUserId(), updatePostRequest.getContent());

        updatePostRequest.setContent(updatedHtmlContent);

        post.getTags().forEach(tag -> {
            if(!updatePostRequest.getTags().contains(tag.getTitle())) {
                postService.removePostTagAndDecreaseTagCount(postId, tag.getId());
            }
        });

        if(post.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }

        return postService.publishPost(postId, updatePostRequest.getTitle(), updatePostRequest.getSummary(), updatePostRequest.getContent(), updatePostRequest.getThumbnailId(), post.getPublishedAt(), updatePostRequest.getStatus(), updatePostRequest.getTags());
    }

    @DeleteMapping("/{postId}")
    void deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID postId) {
    }
}
