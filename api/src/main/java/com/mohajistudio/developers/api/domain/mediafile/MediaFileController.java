package com.mohajistudio.developers.api.domain.mediafile;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/media-files")
@RequiredArgsConstructor
public class MediaFileController {
    private final MediaFileService mediaFileService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    List<MediaFile> addMediaFiles(@RequestPart(value = "files", required = false) List<MultipartFile> files) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (files == null || files.isEmpty()) {
            throw new CustomException(ErrorCode.MULTIPART_FILE_EXCEPTION);
        }

        String email = userDetails.getUsername();

        return mediaFileService.uploadMediaFiles(email, files);
    }
}
