import {ContactApi} from "../../js/http/ContactApi";
import {Contact, CreateContactParams, ContactFieldsRequest, ContactFields, SubmitFieldsRequest,
  ConcessionType as ConcessionTypeModel, ContactId, CreateParentChildrenRequest} from "../../js/model";

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
      id: "5138961",
      uniqueIdentifier: "5qHShQVb3a6nnYn2",
      firstName: "John",
      lastName: "Shepard",
      email: "tuchanka@tut.by",
    } as Contact);
  }

  createOrGetContact(request: CreateContactParams): Promise<Contact> {
    console.log(request);
    const result:ContactId = new ContactId();
    let contact: Contact = this.config.db.getContactByDetails(request.firstName, request.lastName, request.email);
    if (!contact) {
      contact = mockContact();
      contact.firstName = request.firstName;
      contact.lastName = request.lastName;
      contact.email = request.email;
      this.config.db.addContact(contact);
      result.newContact = true;
    } else {
      result.newContact = false;
    }

    result.parentRequired = request.firstName == 'child' || request.firstName == 'childp';

    result.parent = request.firstName == 'childp'
      ? this.config.db.contacts.entities.contact[this.config.db.contacts.result[0]]
      : null;

    result.id = contact.id;
    return this.config.createResponse(result);
  }

  getContactFields(request: ContactFieldsRequest): Promise<ContactFields> {
    const result: ContactFields = {
      contactId: request.contactId,
      headings: [],
    };

    if (!this.config.props.contactApi.contactFieldsIsEmpty) {
      result.headings.push(this.config.db.getFieldHeadingBy(["street", "postcode", "suburb", "country", "citizenship",
        "languageHome", "isMale", "isMarketingViaEmailAllowed", "priorEducationCode", "customField.contact.passportNumber"]));
    }

    return this.config.createResponse(result);
  }

  createParentChildrenRelation(contactFields: CreateParentChildrenRequest): Promise<any> {
    return this.config.createResponse(
      `success bind parent ${contactFields.parentId} width childs ${contactFields.childrenIds}`,
    );
  }

  getConcessionTypes() {
    // move to mock db
    const result1: ConcessionTypeModel = new ConcessionTypeModel();
    result1.id = '1';
    result1.name = 'Student';
    const result2: ConcessionTypeModel = new ConcessionTypeModel();
    result2.id = '2';
    result2.name = 'Man';
    result2.hasExpireDate = true;
    result2.hasNumber = true;

    return this.config.createResponse([result2, result1]);
  }

  getContactConcessions(ids) {
    const result1: ConcessionTypeModel = new ConcessionTypeModel();
    result1.id = '1';
    result1.name = 'Student';
    return this.config.createResponse({[ids[0]]: [result1]});
  }


  submitContactDetails(submit: SubmitFieldsRequest): Promise<ContactId> {
    const contactId = new ContactId();
    contactId.id = submit.contactId;
    contactId.parentRequired = false;

    return this.config.createResponse(Promise.resolve(contactId));
  }

  submitConcession(request) {
    return this.config.createResponse(Promise.resolve());
  }

}

