package com.mohajistudio.developers.infra.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.MediaFile;
import com.mohajistudio.developers.database.enums.ContentType;
import com.mohajistudio.developers.database.repository.mediafile.MediaFileRepository;
import com.mohajistudio.developers.infra.util.UploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {
    private final AmazonS3Client amazonS3Client;
    private final MediaFileRepository mediaFileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public MediaFile uploadImage(UUID memberId, MultipartFile file) {
        String contentType = file.getContentType();

        String fileName;
        if (contentType != null && contentType.startsWith("image/")) {
            fileName = UploadUtil.createImagesFileName(file.getOriginalFilename(), memberId);
        } else if (contentType != null && contentType.startsWith("video/")) {
            fileName = UploadUtil.createVideosFileName(file.getOriginalFilename(), memberId);
        } else {
            throw new CustomException(ErrorCode.INVALID_MEDIA_TYPE);
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata);
            amazonS3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.STORAGE_UPLOAD_FAILURE);
        }

        MediaFile mediaFile = MediaFile.builder().userId(memberId).fileName(fileName).contentType(ContentType.fromMimeType(contentType)).size(file.getSize()).build();

        return mediaFileRepository.save(mediaFile);
    }

    public MediaFile findByIdAndUserId(UUID mediaFileId, UUID memberId) {
        return mediaFileRepository.findByIdAndUserId(mediaFileId, memberId);
    }
}
