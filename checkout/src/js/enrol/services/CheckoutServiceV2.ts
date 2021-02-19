import {CheckoutV2Api} from "../../http/CheckoutV2Api";
import {DefaultHttpService} from "../../common/services/HttpService";
import {Contact, ContactNode, ContactNodeRequest, PaymentRequest, PaymentResponse, PurchaseItem} from "../../model";
import {CartState, IshState} from "../../services/IshState";
import {BuildContactNodeRequest} from "./CheckoutService";
import {ContactNodeService} from "./ContactNodeService";
import {State as SummaryState} from "../containers/summary/reducers/State";

class CheckoutServiceV2 {
  readonly checkoutApi = new CheckoutV2Api(new DefaultHttpService());


  public makePayment(paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string): Promise<PaymentResponse> {
    return this.checkoutApi.makePayment(paymentRequest,xValidateOnly,payerId);
  }
  public getStatus(sessionId: string, payerId: string): Promise<PaymentResponse> {
    return this.checkoutApi.getStatus(sessionId,payerId);
  }

  public getContactNode = (contact: Contact, summary: SummaryState, cart: CartState): Promise<ContactNode> => {
    return this.checkoutApi.getContactNodeV2(BuildContactNodeRequest.fromContact(contact, summary, cart));
  }

  public updateItem = (item: PurchaseItem, state: IshState): Promise<PurchaseItem> => {
    if (item.selected) {
      const request: ContactNodeRequest = BuildContactNodeRequest.fromPurchaseItem(item, state);
      return this.checkoutApi.getContactNodeV2(request)
          .then(data => {
            return Promise.resolve(ContactNodeService.getPurchaseItem(data, item));
          });
    } else {
      return Promise.resolve(item);
    }
  }
}

export default new CheckoutServiceV2();
