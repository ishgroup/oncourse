import {HttpService} from "../common/services/HttpService";
import {CreateParentChildrenRequest, Concession, ConcessionType, ContactFields, ContactFieldsRequest,
  SubmitFieldsRequest, Contact, ContactId, CreateContactParams} from "../model";

export class ContactApi {
  constructor(private http: HttpService) {
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
  getContactConcessions(contactIds: string[]): Promise<Concession[]> {
    return this.http.POST(`/getContactConcessions`, contactIds);
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
