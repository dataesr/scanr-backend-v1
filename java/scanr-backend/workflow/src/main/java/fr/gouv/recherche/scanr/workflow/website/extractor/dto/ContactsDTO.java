/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.website.extractor.dto;

import java.util.List;

public class ContactsDTO {
    public List<Contact> contacts;
    public String companyId;

    public ContactsDTO(List<Contact> c, String companyId) {
        this.contacts = c;
        this.companyId = companyId;
    }

    public ContactsDTO() {}
}