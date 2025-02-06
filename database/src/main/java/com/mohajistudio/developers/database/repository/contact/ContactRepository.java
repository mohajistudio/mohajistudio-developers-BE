package com.mohajistudio.developers.database.repository.contact;

import com.mohajistudio.developers.database.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID>, ContactCustomRepository {
}