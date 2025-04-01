package com.mohajistudio.developers.database.repository.contact;

import com.mohajistudio.developers.database.entity.Contact;

import java.util.List;
import java.util.UUID;

public interface ContactCustomRepository {
    List<Contact> findByUserId(UUID userId);
}
