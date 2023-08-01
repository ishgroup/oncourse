import { PaymentOut, PaymentOutApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class PaymentOutService {
  readonly paymentOutApi = new PaymentOutApi(new DefaultHttpService());

  public getPaymentOut(id: number): Promise<any> {
    return this.paymentOutApi.get(id);
  }

  public updatePaymentOut(id: number, payment: PaymentOut): Promise<any> {
    return this.paymentOutApi.update(payment);
  }

  public postPaymentOut(payment: PaymentOut): Promise<any> {
    return this.paymentOutApi.create(payment);
  }
}

export default new PaymentOutService();
