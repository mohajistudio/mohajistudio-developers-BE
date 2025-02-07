package com.mohajistudio.developers.api.domain.post;

import com.mohajistudio.developers.api.domain.post.dto.request.CreatePostRequest;
import com.mohajistudio.developers.api.domain.post.dto.request.UpdatePostRequest;
import com.mohajistudio.developers.authentication.dto.CustomUserDetails;
import com.mohajistudio.developers.common.dto.response.CustomPageResponse;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.common.utils.HttpUtil;
import com.mohajistudio.developers.database.dto.PostDetailsDto;
import com.mohajistudio.developers.database.dto.PostDto;
import com.mohajistudio.developers.database.entity.MediaFile;
import com.mohajistudio.developers.database.entity.Post;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    CustomPageResponse<PostDto> getPosts(Pageable pageable) {
        Page<PostDto> posts = postService.findAllPost(pageable);

        return new CustomPageResponse<>(posts);
    }

    @PostMapping
    UUID addPost(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CreatePostRequest createPostRequest) {
        String updatedHtmlContent = postService.processHtmlImagesForPermanentStorage(userDetails.getUserId(), createPostRequest.getContent());

        createPostRequest.setContent(updatedHtmlContent);

        return postService.publishPost(userDetails.getUserId(), createPostRequest.getTitle(), createPostRequest.getSummary(), createPostRequest.getContent(), createPostRequest.getThumbnailId(), createPostRequest.getStatus(), createPostRequest.getTags());
    }

    @PostMapping(value = "/media", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    List<MediaFile> addMedia(@RequestPart(value = "files", required = false) List<MultipartFile> files) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (files == null || files.isEmpty()) {
            throw new CustomException(ErrorCode.MULTIPART_FILE_EXCEPTION);
        }

        String email = userDetails.getUsername();

        return postService.uploadMediaFiles(email, files);
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

    @PatchMapping("/{postId}")
    Post updatePost(@PathVariable UUID postId, @RequestBody UpdatePostRequest post) {
        return null;
    }

    @DeleteMapping("/{postId}")
    void deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID postId) {
    }
}
