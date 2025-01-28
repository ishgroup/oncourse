/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import {
  CartIds,
  CheckoutApi,
  CheckoutModel,
  CheckoutResponse,
  CheckoutSaleRelation,
  CourseClassDiscount,
  SessionStatus
} from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class CheckoutService {
  readonly httpService = new DefaultHttpService();

  readonly checkoutApi = new CheckoutApi(this.httpService);

  readonly controller = new AbortController();
  
  public createSession(checkoutModel: CheckoutModel): Promise<CheckoutResponse> {
    // this.controller.abort("Aborted");
    return this.checkoutApi.createSession(checkoutModel, window.location.origin);
  }

  public getContactDiscounts(contactId: number, classId: number, courseIds: string, productIds: string, classIds: string, promoIds: string, membershipIds: string, purchaseTotal: number, payerId: number = null): Promise<CourseClassDiscount[]> {
    return this.checkoutApi.getContactDiscounts(contactId, classId, courseIds, productIds, classIds, promoIds, membershipIds, purchaseTotal, payerId);
  }

  public getSessionStatus(sessionId: string): Promise<SessionStatus> {
    return this.httpService.GET(`/v1/checkout/status/${sessionId}`, { headers: {  }, params: { }, signal: this.controller.signal });
  }

  public getSaleRelations(courseIds: string, productIds: string, contactId: number): Promise<CheckoutSaleRelation[]> {
    return this.checkoutApi.getSaleRelations(courseIds, productIds, contactId);
  }

  getCartDataIds(checkoutId: number): Promise<CartIds> {
    return this.checkoutApi.getCartDataIds(checkoutId);
  }

  getClientKey(): Promise<any> {
    return this.checkoutApi.getClientKey();
  }
}

export default new CheckoutService();
