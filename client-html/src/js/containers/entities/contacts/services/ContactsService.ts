/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  Contact,
  ContactApi,
  ContactMergeApi,
  ContactRelationType,
  MergeData,
  MergeRequest,
  ConcessionApi,
  ConcessionType,
  TaxApi,
  Tax,
  UsiVerificationResult,
  Diff,
  ContactInsightApi,
  ContactInsight
} from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class ContactsService {
  readonly contactMergeApi = new ContactMergeApi(new DefaultHttpService());

  readonly contactApi = new ContactApi(new DefaultHttpService());

  readonly contactInsightApi = new ContactInsightApi(new DefaultHttpService());

  readonly concessionApi = new ConcessionApi(new DefaultHttpService());

  readonly taxApi = new TaxApi(new DefaultHttpService());

  public getContact(id: number): Promise<Contact> {
    return this.contactApi.getContact(id);
  }

  public getInsight(id: number): Promise<ContactInsight> {
    return this.contactInsightApi.getInsight(id);
  }

  public createContact(contact: Contact): Promise<any> {
    return this.contactApi.createContact(contact);
  }

  public deleteContact(id: number): Promise<any> {
    return this.contactApi.removeContact(id);
  }

  public updateContact(id: number, contact: Contact): Promise<Contact> {
    return this.contactApi.updateContact(id, contact);
  }

  public getMergeData(contactA, contactB): Promise<MergeData> {
    return this.contactMergeApi.getMergeData(contactA, contactB);
  }

  public merge(mergeRequest: MergeRequest): Promise<any> {
    return this.contactMergeApi.merge(mergeRequest);
  }

  public getContactsRelationTypes(): Promise<ContactRelationType[]> {
    return this.contactApi.get();
  }

  public bulkChange(diff: Diff): Promise<any> {
    return this.contactApi.bulkChange(diff);
  }

  public verifyUSI(
    firstName: string,
    lastName: string,
    birthDate: string,
    usiCode: string
  ): Promise<UsiVerificationResult> {
    return this.contactApi.verifyUsi(
      {
        firstName, lastName, birthDate, usiCode
      }
    );
  }

  public getContactsConcessionTypes(): Promise<ConcessionType[]> {
    return this.concessionApi.get();
  }

  public getContactsTaxTypes(): Promise<Tax[]> {
    return this.taxApi.get();
  }
}

export default new ContactsService();
