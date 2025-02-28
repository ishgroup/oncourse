/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import {
  CartIds,
  CheckoutApi,
  CheckoutCCResponse,
  CheckoutModel,
  CheckoutResponse,
  CheckoutSaleRelation,
  CheckoutSubmitRequest, ClientPreferences,
  CourseClassDiscount,
  CreateSessionResponse,
  SessionStatus
} from '@api/model';
import { DefaultHttpService } from '../../../common/services/HttpService';

class CheckoutService {
  readonly checkoutApi = new CheckoutApi(new DefaultHttpService());
  
  public createSession(checkoutModel: CheckoutModel, deprecatedSessionId: string = null): Promise<CreateSessionResponse> {
    return this.checkoutApi.createSession(checkoutModel, window.location.origin, deprecatedSessionId);
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
  
  public submitPayment(checkoutModel: CheckoutModel): Promise<CheckoutResponse> {
    return this.checkoutApi.submitPayment(checkoutModel);
  }

  public submitCreditCardPayment(submitRequest: CheckoutSubmitRequest): Promise<CheckoutCCResponse> {
    return this.checkoutApi.submitCreditCardPayment(submitRequest);
  }

  public getCartDataIds(checkoutId: number): Promise<CartIds> {
    return this.checkoutApi.getCartDataIds(checkoutId);
  }

  public getClientPreferences(): Promise<ClientPreferences> {
    return this.checkoutApi.getClientPreferences();
  }
  
  public submitPaymentRedirect(sessionId: string, key: string): Promise<string> {
    return this.checkoutApi.submitPaymentRedirect(sessionId, key);
  }
}

export default new CheckoutService();
