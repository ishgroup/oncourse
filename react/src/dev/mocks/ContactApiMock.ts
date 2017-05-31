import {ContactApi} from "../../js/http/ContactApi";
import {Contact} from "../../js/model/web/Contact";
import {CreateContactParams} from "../../js/model/web/CreateContactParams";
import {ContactFieldsRequest} from "../../js/model/field/ContactFieldsRequest";
import {ContactFields} from "../../js/model/field/ContactFields";
import {SubmitFieldsRequest} from "../../js/model/field/SubmitFieldsRequest";

import {MockConfig} from "./mocks/MockConfig";
import uuid from "uuid";
import {mockContact} from "./mocks/MockFunctions";
export class ContactApiMock extends ContactApi {

  public config: MockConfig;

  public id: string = uuid();

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

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
    let contact: Contact = this.config.db.getContactByDetails(request.firstName, request.lastName, request.email);
    if (!contact) {
      contact = mockContact();
      contact.firstName = request.firstName;
      contact.lastName = request.lastName;
      contact.email = request.email;
      this.config.db.addContact(contact);
    }
    return this.config.createResponse(contact.id);
  }

  getContactFields(request: ContactFieldsRequest): Promise<ContactFields> {
    const result: ContactFields = {
      contactId: request.contactId,
      headings: []
    };

    if (!this.config.props.contactApi.contactFieldsIsEmpty) {
      result.headings.push(this.config.db.getFieldHeadingBy(["street", "suburb", "country"]));
    }

    return this.config.createResponse(result);
  }


  submitContactDetails(submit: SubmitFieldsRequest): Promise<any> {
    return this.config.createResponse(Promise.resolve());
  }

}

