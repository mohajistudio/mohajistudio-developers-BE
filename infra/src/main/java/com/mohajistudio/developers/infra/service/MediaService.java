package com.mohajistudio.developers.infra.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.MediaFile;
import com.mohajistudio.developers.database.enums.ContentType;
import com.mohajistudio.developers.infra.util.MediaUtil;
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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public MediaFile uploadMediaFileToTempFolder(UUID memberId, MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null) {
            throw new CustomException(ErrorCode.INVALID_MEDIA_TYPE);
        }

        if (!contentType.startsWith("image/") && !contentType.startsWith("video/")) {
            throw new CustomException(ErrorCode.INVALID_MEDIA_TYPE);
        }

        String fileName = MediaUtil.createTempFileName(file.getOriginalFilename(), memberId);

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

        return MediaFile.builder().userId(memberId).fileName(fileName).contentType(ContentType.fromMimeType(contentType)).size(file.getSize()).build();
    }

    public MediaFile moveToPermanentFolder(MediaFile mediaFile) {
        String sourceKey = mediaFile.getFileName();
        String mimeType = mediaFile.getContentType().getMimeType();
        String destinationKey;

        if (mimeType.startsWith("image/")) {
            destinationKey = MediaUtil.changeTempToImages(sourceKey);
        } else if (mimeType.startsWith("video/")) {
            destinationKey = MediaUtil.changeTempToVideos(sourceKey);
        } else {
            throw new CustomException(ErrorCode.INVALID_MEDIA_TYPE);
        }

        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, sourceKey, bucket, destinationKey);
            amazonS3Client.copyObject(copyObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.STORAGE_UPLOAD_FAILURE);
        }

        mediaFile.setFileName(destinationKey);
        return mediaFile;
    }

    public void delete(MediaFile mediaFile) {
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, mediaFile.getFileName());

            amazonS3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.STORAGE_DELETE_FAILURE);
        }
    }
}
