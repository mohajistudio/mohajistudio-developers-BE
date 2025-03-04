package com.mohajistudio.developers.database.repository.contacttype;

import com.mohajistudio.developers.database.dto.ContactTypeDto;

import java.util.List;

public interface ContactTypeCustomRepository {
    List<ContactTypeDto> findAllContactTypeDto();
}
