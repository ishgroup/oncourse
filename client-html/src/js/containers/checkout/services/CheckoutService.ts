/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import {
  CheckoutApi, CheckoutModel, CheckoutResponse, CheckoutSaleRelation, CourseClassDiscount, SessionStatus
} from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class CheckoutService {
  readonly checkoutApi = new CheckoutApi(new DefaultHttpService());

  public checkoutSubmitPayment(checkoutModel: CheckoutModel, xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string): Promise<CheckoutResponse> {
    return this.checkoutApi.submit(checkoutModel, xValidateOnly, xPaymentSessionId, xOrigin);
  }

  public getContactDiscounts(contactId: number, classId: number, courseIds: string, productIds: string, promoIds: string, membershipIds: string, enrolmentsCount: number, purchaseTotal: number): Promise<CourseClassDiscount[]> {
    return this.checkoutApi.getContactDiscounts(contactId, classId, courseIds, productIds, promoIds, membershipIds, enrolmentsCount, purchaseTotal);
  }

  public getSessionStatus(sessionId: string): Promise<SessionStatus> {
    return this.checkoutApi.status(sessionId);
  }

  public getSaleRelations(courseIds: string, productIds: string, contactId: number): Promise<CheckoutSaleRelation[]> {
    return this.checkoutApi.getSaleRelations(courseIds, productIds, contactId);
  }
}

export default new CheckoutService();
