package com.mohajistudio.developers.infra.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class MediaUtil {
    private static final String AWS_S3_BASE_URL = "https://mohajistudio-developers.s3.ap-northeast-2.amazonaws.com";
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String IMAGES_DIR = "media/images/";
    private static final String VIDEOS_DIR = "media/videos/";
    private static final String TEMP_DIR = "media/temp/";

    public static String getAwsS3BaseUrl() {
        return AWS_S3_BASE_URL;
    }

    public static String changeTempToImages(String tempPath) {
        return tempPath.replace(TEMP_DIR, IMAGES_DIR);
    }

    public static String changeTempToVideos(String tempPath) {
        return tempPath.replace(TEMP_DIR, VIDEOS_DIR);
    }

    public static String createTempFileName(String originalFileName, UUID memberId) {
        String ext = getFileExtension(originalFileName);
        UUID uuid = UuidCreator.getTimeOrdered();
        return TEMP_DIR + memberId + "/" + uuid + ext;
    }

    public static String extractIdFromFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("S3 file path cannot be null or empty");
        }

        // 마지막 '/' 이후의 파일 이름 추출
        int lastSlashIndex = fileName.lastIndexOf('/');
        if (lastSlashIndex == -1 || lastSlashIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("Invalid S3 file path format");
        }

        String extractFileName = fileName.substring(lastSlashIndex + 1);

        // 파일 이름에서 확장자를 제외한 부분 추출
        int lastDotIndex = extractFileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("File name does not contain an extension");
        }

        return extractFileName.substring(0, lastDotIndex);
    }

    private static String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR));
    }
}