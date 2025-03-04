package com.mohajistudio.developers.api.domain.contacttype;

import com.mohajistudio.developers.database.dto.ContactTypeDto;
import com.mohajistudio.developers.database.repository.contacttype.ContactTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactTypeService {
    private final ContactTypeRepository contactTypeRepository;

    public List<ContactTypeDto> findAll() {
        return contactTypeRepository.findAllContactTypeDto();
    }
}
