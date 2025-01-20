package com.mohajistudio.developers.database.repository.mediafile;

import com.mohajistudio.developers.database.entity.MediaFile;

import java.util.UUID;

public interface MediaFileCustomRepository {
    MediaFile findByIdAndUserId(UUID mediaFileId, UUID userId);
}
