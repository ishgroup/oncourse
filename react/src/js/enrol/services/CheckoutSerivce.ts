import {Contact} from "../../model/web/Contact";
import {CourseClass} from "../../model/web/CourseClass";
import {Product} from "../../model/web/Product";
import {ContactFields} from "../../model/field/ContactFields";
import {Injector} from "../../injector";
import {ContactFieldsRequest} from "../../model/field/ContactFieldsRequest";
import {FieldSet} from "../../model/web/FieldSet";

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