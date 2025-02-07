package com.mohajistudio.developers.database.repository.contacttype;

import com.mohajistudio.developers.database.entity.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactTypeRepository extends JpaRepository<ContactType, UUID>, ContactTypeCustomRepository {
}