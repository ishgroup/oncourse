import { HttpService } from '../common/services/HttpService';
import { ContactNode } from '../model/checkout/ContactNode';
import { ContactNodeRequest } from '../model/checkout/request/ContactNodeRequest';
import { PaymentRequest } from '../model/v2/checkout/payment/PaymentRequest';
import { PaymentResponse } from '../model/v2/checkout/payment/PaymentResponse';

export class CheckoutV2Api {
  constructor(private http: HttpService) {
  }

  getContactNodeV2(contactNodeRequest: ContactNodeRequest): Promise<ContactNode> {
    return this.http.POST('/v2/getContactNode', contactNodeRequest);
  }

  getStatus(sessionId: string, payerId: string): Promise<PaymentResponse> {
    return this.http.GET(`/v2/getPaymentStatus/${sessionId}`, { headers: { payerId } });
  }

  makePaymentV2(paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string): Promise<PaymentResponse> {
    return this.http.POST(
      '/v2/makePayment',
      paymentRequest,
      { headers: { xValidateOnly, payerId, xOrigin: window.location.href } },
    );
  }
}
