import {ContactApi} from "../http/ContactApi";
import {Contact} from "../model/web/Contact";
import {CreateContactParams} from "../model/web/CreateContactParams";
import {ContactFieldsRequest} from "../model/field/ContactFieldsRequest";
import {ContactFields} from "../model/field/ContactFields";

import * as MockContactFields from "./mocks/MockContactFields";
import {SubmitFieldsRequest} from "../model/field/SubmitFieldsRequest";

import {MockConfig} from "./mocks/MockConfig";
import uuid from "uuid";
import {config} from "shelljs";
import {mockContact} from "./mocks/MockFunctions";
export class ContactApiStub extends ContactApi {

  public config: MockConfig = MockConfig.CONFIG;

  public id: string = uuid();

  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return Promise.resolve({
      "id": "5138961",
      "uniqueIdentifier": "5qHShQVb3a6nnYn2",
      "firstName": "John",
      "lastName": "Shepard",
      "email": "tuchanka@tut.by"
    } as Contact);
  }

  createOrGetContact(request: CreateContactParams): Promise<Contact> {
    let contact:Contact = this.config.db.getContactByDetails(request.firstName, request.lastName, request.email);
    if (!contact) {
      contact = mockContact();
      contact.firstName = request.firstName;
      contact.lastName = request.lastName;
      contact.email = request.email;
      this.config.db.addContact(contact);
    }
    return this.config.createResponse(contact.id);
  }

  getContactFields(contactFieldsRequest: ContactFieldsRequest): Promise<ContactFields> {
    return this.config.createResponse(MockContactFields.ContactFieldsRequest);
  }


  submitContactDetails(submit: SubmitFieldsRequest): Promise<any> {
    return this.config.createResponse(Promise.resolve());
  }

}

