import {HttpService} from "../common/services/HttpService";
import {ConcessionsAndMemberships} from "../model/checkout/ConcessionsAndMemberships";
import {CreateParentChildrenRequest} from "../model/checkout/CreateParentChildrenRequest";
import {Concession} from "../model/checkout/concession/Concession";
import {ConcessionType} from "../model/checkout/concession/ConcessionType";
import {ContactFields} from "../model/field/ContactFields";
import {ContactFieldsRequest} from "../model/field/ContactFieldsRequest";
import {Contact} from "../model/web/Contact";
import {ContactId} from "../model/web/ContactId";
import {CreateContactParams} from "../model/web/CreateContactParams";

export class ContactApi {
  constructor(private http: HttpService) {
  }

  createOrGetContact(createContactParams: CreateContactParams): Promise<ContactId> {
    return this.http.PUT(`/v1/contact`, createContactParams);
  }
  createParentChildrenRelation(request: CreateParentChildrenRequest): Promise<any> {
    return this.http.PUT(`/v1/createParentChildrenRelation`, request);
  }
  getConcessionTypes(): Promise<ConcessionType[]> {
    return this.http.GET(`/v1/getConcessionTypes`);
  }
  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return this.http.GET(`/v1/contact/${studentUniqueIdentifier}`);
  }
  getContactConcessionsAndMemberships(contactIds: string[]): Promise<ConcessionsAndMemberships> {
    return this.http.POST(`/v1/getContactConcessionsAndMemberships`, contactIds);
  }
  getContactFields(contactFieldsRequest: ContactFieldsRequest): Promise<ContactFields> {
    return this.http.POST(`/v1/contactFields`, contactFieldsRequest);
  }
  submitConcession(concession: Concession): Promise<any> {
    return this.http.PUT(`/v1/submitConcession`, concession);
  }
  submitContactDetails(contactFields: ContactFields): Promise<ContactId> {
    return this.http.POST(`/v1/submitContactDetails`, contactFields);
  }
}
