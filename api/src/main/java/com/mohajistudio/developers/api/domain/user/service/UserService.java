package com.mohajistudio.developers.api.domain.user.service;

import com.mohajistudio.developers.api.domain.user.dto.request.UpdateUserRequest;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.UserDetailsDto;
import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.entity.Contact;
import com.mohajistudio.developers.database.entity.ContactType;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.enums.Role;
import com.mohajistudio.developers.database.repository.contact.ContactRepository;
import com.mohajistudio.developers.database.repository.contacttype.ContactTypeRepository;
import com.mohajistudio.developers.database.repository.user.UserRepository;
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
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final ContactTypeRepository contactTypeRepository;

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

        user.setNickname(updateUserRequest.getNickname());
        user.setJobRole(updateUserRequest.getJobRole());
        user.setBio(updateUserRequest.getBio());
        userRepository.save(user);

        updateUserRequest.getContact().forEach(contact -> {
            Optional<ContactType> findContactType = contactTypeRepository.findById(contact.getContactTypeId());

            if (findContactType.isEmpty()) {
                throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "알 수 없는 ContactType");
            }

            Contact newContact = Contact.builder().userId(userId).contactTypeId(contact.getContactTypeId()).displayName(contact.getDisplayName()).url(contact.getUrl()).build();
            contactRepository.save(newContact);
        });
    }

    public UserDetailsDto findUserDetails(String userId) {
        UserDetailsDto userDetails = userRepository.findUserDetailsDto(userId);

        if (userDetails == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return userDetails;
    }
}
