package com.mohajistudio.developers.api.domain.contacttype;

import com.mohajistudio.developers.database.dto.ContactTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contact-types")
@RequiredArgsConstructor
public class ContactTypeController {
    private final ContactTypeService contactTypeService;

    @GetMapping
    List<ContactTypeDto> getContactTypes() {
        return contactTypeService.findAll();
    }
}
