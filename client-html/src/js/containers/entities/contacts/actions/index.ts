/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact, MergeRequest, SearchQuery } from "@api/model";
import { _toRequestType, FULFILLED, REJECTED } from "../../../../common/actions/ActionUtils";

export const GET_MERGE_CONTACTS = _toRequestType("get/mergeContacts");
export const GET_MERGE_CONTACTS_FULFILLED = FULFILLED(GET_MERGE_CONTACTS);

export const CREATE_CONTACT = _toRequestType("post/contact");

export const DELETE_CONTACT = _toRequestType("delete/contact");

export const POST_MERGE_CONTACTS = _toRequestType("post/mergeContacts");
export const POST_MERGE_CONTACTS_FULFILLED = FULFILLED(POST_MERGE_CONTACTS);

export const GET_CONTACTS = _toRequestType("get/contacts");
export const GET_CONTACTS_FULFILLED = FULFILLED(GET_CONTACTS);

export const GET_CONTACT = _toRequestType("get/contact");
export const GET_CONTACT_FULFILLED = FULFILLED(GET_CONTACT);

export const UPDATE_CONTACT = _toRequestType("update/contact");
export const UPDATE_CONTACT_FULFILLED = FULFILLED(UPDATE_CONTACT);

export const GET_CONTACT_TAGS = _toRequestType("get/contact/tags");

export const GET_CONTACTS_RELATION_TYPES = _toRequestType("get/contacts/relationTypes");
export const GET_CONTACTS_RELATION_TYPES_FULFILLED = FULFILLED(GET_CONTACTS_RELATION_TYPES);

export const GET_CONTACTS_CONCESSION_TYPES = _toRequestType("get/contacts/concessionTypes");
export const GET_CONTACTS_CONCESSION_TYPES_FULFILLED = FULFILLED(GET_CONTACTS_CONCESSION_TYPES);

export const GET_CONTACTS_STORED_CC = _toRequestType("get/contacts/storedCC");
export const GET_CONTACTS_STORED_CC_FULFILLED = FULFILLED(GET_CONTACTS_STORED_CC);

export const GET_CONTACTS_TAX_TYPES = _toRequestType("get/contacts/taxTypes");
export const GET_CONTACTS_TAX_TYPES_FULFILLED = FULFILLED(GET_CONTACTS_TAX_TYPES);

export const GET_CONTACT_ENROLMENTS = _toRequestType("get/contact/enrolments");
export const GET_CONTACT_ENROLMENTS_FULFILLED = FULFILLED(GET_CONTACT_ENROLMENTS);

export const GET_CONTACT_PRIOR_LEARNINGS = _toRequestType("get/contact/priorLearnings");
export const GET_CONTACT_PRIOR_LEARNINGS_FULFILLED = FULFILLED(GET_CONTACT_PRIOR_LEARNINGS);

export const GET_CONTACT_OUTCOMES = _toRequestType("get/contact/outcomes");
export const GET_CONTACT_OUTCOMES_FULFILLED = FULFILLED(GET_CONTACT_OUTCOMES);

export const GET_CONTACT_CERTIFICATES = _toRequestType("get/contact/certificates");
export const GET_CONTACT_CERTIFICATES_FULFILLED = FULFILLED(GET_CONTACT_CERTIFICATES);

export const SET_CONTACTS_SEARCH = "set/contacts/search";

export const CLOSE_MERGE_CONTACTS_SUCCESS = "close/contacts/mergeContactsSuccess";

export const VERIFY_USI = _toRequestType("verify/contacts/usi");
export const VERIFY_USI_FULFILLED = FULFILLED(VERIFY_USI);
export const VERIFY_USI_REJECTED = REJECTED(VERIFY_USI);

export const CLEAR_USI_VERIFICATION_RESULT = "clear/contacts/usiVerificationResult";
export const CLEAR_CONTACT_EDUCATION = "clear/contact/education";

export const GET_CONTACTS_MESSAGE_DATA = _toRequestType("get/contacts/messageData");

export const CLEAR_CONTACTS = "clear/contact";

export const postMergeContacts = (request: MergeRequest) => ({
  type: POST_MERGE_CONTACTS,
  payload: request
});

export const getMergeContacts = (contactA: string, contactB: string) => ({
  type: GET_MERGE_CONTACTS,
  payload: { contactA, contactB }
});

export const getContacts = (offset?: number, columns?: string, ascending?: boolean, sort?: string, pageSize?: number) => ({
  type: GET_CONTACTS,
  payload: {
    offset, columns, ascending, sort, pageSize
  }
});

export const clearContacts = () => ({
  type: CLEAR_CONTACTS
});

export const getContact = (id: number) => ({
  type: GET_CONTACT,
  payload: id
});

export const deleteContact = (id: number) => ({
  type: DELETE_CONTACT,
  payload: id
});

export const getContactsStoredCc = (id: number) => ({
  type: GET_CONTACTS_STORED_CC,
  payload: id
});

export const createContact = (contact: Contact) => ({
  type: CREATE_CONTACT,
  payload: contact
});

export const updateContact = (id: string, contact: Contact) => ({
  type: UPDATE_CONTACT,
  payload: { id, contact }
});

export const getContactTags = () => ({
  type: GET_CONTACT_TAGS
});

export const getContactEnrolments = (contactId: number) => ({
  type: GET_CONTACT_ENROLMENTS,
  payload: contactId
});

export const getContactPriorLearnings = (contactId: number) => ({
  type: GET_CONTACT_PRIOR_LEARNINGS,
  payload: contactId
});

export const getContactOutcomes = (contactId: number) => ({
  type: GET_CONTACT_OUTCOMES,
  payload: contactId
});

export const getContactCertificates = (contactId: number) => ({
  type: GET_CONTACT_CERTIFICATES,
  payload: contactId
});

export const setContactsSearch = (search: string) => ({
  type: SET_CONTACTS_SEARCH,
  payload: { search }
});

export const clearContactsSearch = () => ({
  type: SET_CONTACTS_SEARCH,
  payload: { search: "", items: [] }
});

export const closeMergeContactsSuccess = () => ({
  type: CLOSE_MERGE_CONTACTS_SUCCESS
});

export const getContactsRelationTypes = () => ({
  type: GET_CONTACTS_RELATION_TYPES
});

export const getContactsConcessionTypes = () => ({
  type: GET_CONTACTS_CONCESSION_TYPES
});

export const getContactsTaxTypes = () => ({
  type: GET_CONTACTS_TAX_TYPES
});

export const verifyUSI = (firstName: string, lastName: string, birthDate: string, usiCode: string) => ({
  type: VERIFY_USI,
  payload: {
    firstName, lastName, birthDate, usiCode
  }
});

export const clearUSIVerificationResult = () => ({
  type: CLEAR_USI_VERIFICATION_RESULT
});

export const clearContactEducation = () => ({
  type: CLEAR_CONTACT_EDUCATION
});

export const getContactsMessageData = (searchQuery: SearchQuery, selection: string[]) => ({
  type: GET_CONTACTS_MESSAGE_DATA,
  payload: { filteredSearch: searchQuery, selection }
});
