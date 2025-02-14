package com.mohajistudio.developers.api.domain.post;

import com.mohajistudio.developers.api.domain.post.dto.request.CreatePostRequest;
import com.mohajistudio.developers.api.domain.post.dto.request.GenerateSummaryRequest;
import com.mohajistudio.developers.api.domain.post.dto.request.GetPostRequest;
import com.mohajistudio.developers.api.domain.post.dto.request.UpdatePostRequest;
import com.mohajistudio.developers.authentication.dto.CustomUserDetails;
import com.mohajistudio.developers.common.dto.response.CustomPageResponse;
import com.mohajistudio.developers.common.utils.HttpUtil;
import com.mohajistudio.developers.database.dto.PostDetailsDto;
import com.mohajistudio.developers.database.dto.PostDto;
import com.mohajistudio.developers.database.entity.Post;
import com.mohajistudio.developers.database.enums.PostStatus;
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
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final AzureOpenAiService azureOpenAiService;

    @GetMapping
    CustomPageResponse<PostDto> getPosts(Pageable pageable, GetPostRequest getPostRequest) {
        Page<PostDto> posts = postService.findAllPost(pageable, getPostRequest.getUserId(), getPostRequest.getSearch(), getPostRequest.getTags(), PostStatus.PUBLISHED);

        return new CustomPageResponse<>(posts);
    }

    @PostMapping
    UUID addPost(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CreatePostRequest createPostRequest) {
        String updatedHtmlContent = postService.processHtmlImagesForPermanentStorage(userDetails.getUserId(), createPostRequest.getContent());

        createPostRequest.setContent(updatedHtmlContent);

        return postService.publishPost(userDetails.getUserId(), createPostRequest.getTitle(), createPostRequest.getSummary(), createPostRequest.getContent(), createPostRequest.getThumbnailId(), createPostRequest.getStatus(), createPostRequest.getTags());
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
    Post updatePost(@PathVariable UUID postId, @RequestBody UpdatePostRequest post) {
        return null;
    }

    @DeleteMapping("/{postId}")
    void deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID postId) {
    }
}
