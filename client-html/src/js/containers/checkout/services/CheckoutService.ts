/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import {
 CheckoutApi, CheckoutModel, CheckoutResponse, CourseClassDiscount, SessionStatus
} from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class CheckoutService {
  readonly checkoutApi = new CheckoutApi(new DefaultHttpService());

  public checkoutSubmitPayment(checkoutModel: CheckoutModel, xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string): Promise<CheckoutResponse> {
    return this.checkoutApi.submit(checkoutModel, xValidateOnly, xPaymentSessionId, xOrigin);
  }

  public getContactDiscounts(contactId: number, classId: number, promoIds: string, membershipIds: string, enrolmentsCount: number, purchaseTotal: number): Promise<CourseClassDiscount[]> {
    return this.checkoutApi.getContactDiscounts(contactId, classId, promoIds, membershipIds, enrolmentsCount, purchaseTotal);
  }

  public getSessionStatus(sessionId: string): Promise<SessionStatus> {
    return this.checkoutApi.status(sessionId);
  }
}

export default new CheckoutService();
