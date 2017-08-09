import {HttpService} from "../common/services/HttpService";
import {
  Contact, ContactId, SubmitFieldsRequest, ContactFieldsRequest, ConcessionsAndMemberships, ChangeParentRequest,
  ContactFields, CommonError, ValidationError, ConcessionType, Concession, CreateParentChildrenRequest,
  CreateContactParams,
} from "../model";

export class ContactApi {
  constructor(private http: HttpService) {
  }
  changeParent(request: ChangeParentRequest): Promise<any> {
    return this.http.PUT(`/changeParent`, request);
  }
  createOrGetContact(createContactParams: CreateContactParams): Promise<ContactId> {
    return this.http.PUT(`/contact`, createContactParams);
  }
  createParentChildrenRelation(request: CreateParentChildrenRequest): Promise<any> {
    return this.http.PUT(`/createParentChildrenRelation`, request);
  }
  getConcessionTypes(): Promise<ConcessionType[]> {
    return this.http.GET(`/getConcessionTypes`);
  }
  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return this.http.GET(`/contact/${studentUniqueIdentifier}`);
  }
  getContactConcessionsAndMemberships(contactIds: string[]): Promise<ConcessionsAndMemberships> {
    return this.http.POST(`/getContactConcessionsAndMemberships`, contactIds);
  }
  getContactFields(contactFieldsRequest: ContactFieldsRequest): Promise<ContactFields> {
    return this.http.POST(`/contactFields`, contactFieldsRequest);
  }
  submitConcession(concession: Concession): Promise<any> {
    return this.http.PUT(`/submitConcession`, concession);
  }
  submitContactDetails(contactFields: SubmitFieldsRequest): Promise<ContactId> {
    return this.http.POST(`/submitContactDetails`, contactFields);
  }
}
