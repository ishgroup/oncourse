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
  readonly checkoutApi = new CheckoutApi(new DefaultHttpService());
  
  public createSession(checkoutModel: CheckoutModel): Promise<CheckoutResponse> {
    return this.checkoutApi.createSession(checkoutModel, window.location.origin);
  }

  public checkoutSubmitPayment(checkoutModel: CheckoutModel, xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string): Promise<CheckoutResponse> {
    return this.checkoutApi.submit(checkoutModel, xValidateOnly, xPaymentSessionId, xOrigin);
  }

  public getContactDiscounts(contactId: number, classId: number, courseIds: string, productIds: string, classIds: string, promoIds: string, membershipIds: string, purchaseTotal: number, payerId: number = null): Promise<CourseClassDiscount[]> {
    return this.checkoutApi.getContactDiscounts(contactId, classId, courseIds, productIds, classIds, promoIds, membershipIds, purchaseTotal, payerId);
  }

  public getSessionStatus(sessionId: string): Promise<SessionStatus> {
    return this.checkoutApi.status(sessionId);
  }

  public getSaleRelations(courseIds: string, productIds: string, contactId: number): Promise<CheckoutSaleRelation[]> {
    return this.checkoutApi.getSaleRelations(courseIds, productIds, contactId);
  }

  public updateModel(checkoutModel: CheckoutModel): Promise<CheckoutResponse> {
    return this.checkoutApi.updateModel(checkoutModel);
  }
  
  public submitPayment(xPaymentSessionId: string): Promise<CheckoutResponse> {
    return this.submitPayment(xPaymentSessionId);
  }

  getCartDataIds(checkoutId: number): Promise<CartIds> {
    return this.checkoutApi.getCartDataIds(checkoutId);
  }

  getClientKey(): Promise<any> {
    return this.checkoutApi.getClientKey();
  }
}

export default new CheckoutService();
