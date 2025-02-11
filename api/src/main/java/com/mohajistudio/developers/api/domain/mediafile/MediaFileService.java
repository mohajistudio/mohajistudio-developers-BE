package com.mohajistudio.developers.api.domain.mediafile;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.MediaFile;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.repository.mediafile.MediaFileRepository;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import com.mohajistudio.developers.infra.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaFileService {
    private final UserRepository userRepository;
    private final MediaService mediaService;
    private final MediaFileRepository mediaFileRepository;

    public List<MediaFile> uploadMediaFiles(String email, List<MultipartFile> files) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        List<MediaFile> mediaFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            MediaFile mediaFile = mediaService.uploadMediaFileToTempFolder(user.getId(), file);

            MediaFile savedMediaFile = mediaFileRepository.save(mediaFile);

            mediaFiles.add(savedMediaFile);
        }

        return mediaFiles;
    }
}
