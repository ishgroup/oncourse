import * as L from "lodash";

import {Contact} from "../../model/web/Contact";
import {CourseClass} from "../../model/web/CourseClass";
import {Product} from "../../model/web/Product";
import {ContactFields} from "../../model/field/ContactFields";
import {Injector} from "../../injector";
import {ContactFieldsRequest} from "../../model/field/ContactFieldsRequest";
import {FieldSet} from "../../model/field/FieldSet";
import {SubmitFieldsRequest} from "../../model/field/SubmitFieldsRequest";
import {CreateContactParams} from "../../model/web/CreateContactParams";

const {
  contactApi,
} = Injector.of();


export const loadFields = (contact: Contact, classes: CourseClass[] = [], products: Product[] = []): Promise<ContactFields> => {
  const request: ContactFieldsRequest = new ContactFieldsRequest();
  request.contactId = contact.id;
  request.classesIds = classes.map((c) => c.id);
  request.fieldSet = FieldSet.enrolment;
  return contactApi.getContactFields(new ContactFieldsRequest());
};


export const submitContactDetails = (fields: ContactFields, values: any): Promise<any> => {
  const request: SubmitFieldsRequest = new SubmitFieldsRequest();
  request.contactId = fields.contactId;
  request.fields = L.flatMap(fields.headings, (h) => {
    return h.fields
  });
  return contactApi.submitContactDetails(request);
};

export const createOrGetContact = (values: any): Promise<string> => {
  const request:CreateContactParams = Object.assign({}, values, {fieldSet: FieldSet.enrolment});
  return contactApi.createOrGetContact(request);
};

