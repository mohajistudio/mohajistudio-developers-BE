package com.mohajistudio.developers.api.domain.user;

import com.mohajistudio.developers.api.domain.user.dto.request.UpdateUserRequest;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.UserDetailsDto;
import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.entity.Contact;
import com.mohajistudio.developers.database.entity.ContactType;
import com.mohajistudio.developers.database.entity.MediaFile;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.enums.Role;
import com.mohajistudio.developers.database.repository.contact.ContactRepository;
import com.mohajistudio.developers.database.repository.contacttype.ContactTypeRepository;
import com.mohajistudio.developers.database.repository.mediafile.MediaFileRepository;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import com.mohajistudio.developers.infra.service.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final StorageService storageService;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final MediaFileRepository mediaFileRepository;

    public Page<UserDto> findAllUser(Pageable pageable, Role role) {
        return userRepository.findAllUserDto(pageable, role);
    }

    @Transactional
    public void updateUser(UUID userId, UpdateUserRequest updateUserRequest) {
        Optional<User> findUser = userRepository.findById(userId);

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        /// 기존에 프로필 이미지가 있고 업데이트 할 프로필 이미지가 다르다면 기존 프로필 이미지를 삭제하고 새로운 MediaFile을 추가함
        /// 기존에 프로필 이미지가 없고 업데이트 할 프로필 이미지가 있다면 MediaFile을 추가함
        if(user.getProfileImageId() != null && !user.getProfileImageId().equals(updateUserRequest.getProfileImageId())) {
            MediaFile findOldMediaFile = mediaFileRepository.findByIdAndUserId(user.getProfileImageId(), userId);

            if(findOldMediaFile == null) {
                throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "유효하지 않은 프로필 이미지");
            }

            storageService.remove(findOldMediaFile.getFileName());

            mediaFileRepository.delete(findOldMediaFile);

            MediaFile findMediaFile = mediaFileRepository.findByIdAndUserId(updateUserRequest.getProfileImageId(), userId);

            if(findMediaFile == null) {
                throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "유효하지 않은 프로필 이미지");
            }

            MediaFile mediaFile = storageService.copyToPermanentFolder(findMediaFile);

            mediaFileRepository.save(mediaFile);

            user.setProfileImageId(mediaFile.getId());
            user.setProfileImageUrl(mediaFile.getFileName());
        } else if(user.getProfileImageId() == null && updateUserRequest.getProfileImageId() != null) {
            MediaFile findMediaFile = mediaFileRepository.findByIdAndUserId(updateUserRequest.getProfileImageId(), userId);

            if(findMediaFile == null) {
                throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "유효하지 않은 프로필 이미지");
            }

            MediaFile mediaFile = storageService.copyToPermanentFolder(findMediaFile);

            mediaFileRepository.save(mediaFile);

            user.setProfileImageId(mediaFile.getId());
            user.setProfileImageUrl(mediaFile.getFileName());
        }

        user.setNickname(updateUserRequest.getNickname());
        user.setJobRole(updateUserRequest.getJobRole());
        user.setBio(updateUserRequest.getBio());
        userRepository.save(user);

        updateUserRequest.getContacts().forEach(contact -> {
            Optional<ContactType> findContactType = contactTypeRepository.findById(contact.getContactTypeId());

            if (findContactType.isEmpty()) {
                throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "알 수 없는 ContactType");
            }

            Contact newContact = Contact.builder().userId(userId).contactTypeId(contact.getContactTypeId()).displayName(contact.getDisplayName()).url(contact.getUrl()).build();
            contactRepository.save(newContact);
        });
    }


    public UserDetailsDto findUserDetails(String nickname) {
        UserDetailsDto userDetails = userRepository.findUserDetailsDto(nickname);

        if (userDetails == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return userDetails;
    }

    public void deleteUser(UUID userId) {
        UserDetailsDto user = userRepository.findUserDetailsDto(userId);

        if(user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        user.getContacts().forEach(contact -> contactRepository.deleteById(contact.getId()));

        userRepository.deleteById(user.getId());
    }
}
