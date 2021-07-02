import {ContactApi} from "../../js/http/ContactApi";
import {Contact, CreateContactParams, ContactFieldsRequest, ContactFields,
  ConcessionType as ConcessionTypeModel, ContactId, CreateParentChildrenRequest} from "../../js/model";
import {MockConfig} from "./mocks/MockConfig";
import uuid from "uuid";
import {mockContact} from "./mocks/MockFunctions";
import {Concession} from "../../js/model/checkout/concession/Concession";
import {ConcessionsAndMemberships} from "../../js/model/checkout/ConcessionsAndMemberships";
import {StudentMembership} from "../../js/model/checkout/StudentMembership";


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
    result.id = contact.id;

    return this.config.createResponse(result);
  }

  getContactFields(request: ContactFieldsRequest): Promise<ContactFields> {
    const result: ContactFields = {
      contactId: request.contactId,
      headings: [],
    };

    if (!this.config.props.contactApi.contactFieldsIsEmpty) {
      result.headings.push(this.config.db.getFieldHeadingBy([
        "customField.contact.passportType", "street", "postcode", "suburb", "isMale", "isMarketingViaEmailAllowed", "country",
        "dateOfBirth", "citizenship", "customField.contact.information",
      ]));
    }

    return this.config.createResponse(result);
  }

  createParentChildrenRelation(contactFields: CreateParentChildrenRequest): Promise<any> {
    return this.config.createResponse(
      `success bind parent ${contactFields.parentId} width childs ${contactFields.childrenIds}`,
    );
  }

  changeParent(): Promise<any> {
    return this.config.createResponse({});
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

  getContactConcessionsAndMemberships(ids): Promise<ConcessionsAndMemberships> {
    const concession1: Concession = new Concession();
    concession1.contactId = ids[0];
    concession1.name = 'Student';

    const membership1: StudentMembership = new StudentMembership();
    membership1.contactId = ids[0];
    membership1.name = 'Studend membership';

    return this.config.createResponse({
      concessions: [concession1],
      memberships: [membership1],
    });
  }


  submitContactDetails(submit: ContactFields): Promise<ContactId> {
    const contactId = new ContactId();
    contactId.id = submit.contactId;
    contactId.parentRequired = false;

    return this.config.createResponse(Promise.resolve(contactId));
  }

  submitConcession(request) {
    return this.config.createResponse(Promise.resolve());
    // return CreatePromiseReject({
    //   formErrors:["This concession is already on file for this student."],
    //   fieldsErrors:[{name:"expiryDate",error:"Expiry date shouldn't be at the past."}],
    // });
  }

}

