package com.mohajistudio.developers.database.repository.mediafile;

import com.mohajistudio.developers.database.entity.MediaFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MediaFileCustomRepository {
    MediaFile findByIdAndUserId(UUID mediaFileId, UUID userId);

    Page<MediaFile> findAllByUserId(Pageable pageable, UUID userId);
}
