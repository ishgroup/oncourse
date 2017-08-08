import {HttpService} from "../common/services/HttpService";
import {ChangeParentRequest} from "../model/checkout/ChangeParentRequest";
import {ConcessionsAndMemberships} from "../model/checkout/ConcessionsAndMemberships";
import {CreateParentChildrenRequest} from "../model/checkout/CreateParentChildrenRequest";
import {Concession} from "../model/checkout/concession/Concession";
import {ConcessionType} from "../model/checkout/concession/ConcessionType";
import {CommonError} from "../model/common/CommonError";
import {ValidationError} from "../model/common/ValidationError";
import {ContactFields} from "../model/field/ContactFields";
import {ContactFieldsRequest} from "../model/field/ContactFieldsRequest";
import {SubmitFieldsRequest} from "../model/field/SubmitFieldsRequest";
import {Contact} from "../model/web/Contact";
import {ContactId} from "../model/web/ContactId";
import {CreateContactParams} from "../model/web/CreateContactParams";

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
