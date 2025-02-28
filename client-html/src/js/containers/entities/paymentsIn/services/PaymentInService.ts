import { PaymentIn, PaymentInApi } from '@api/model';
import { DefaultHttpService } from '../../../../common/services/HttpService';

class PaymentInService {
  readonly paymentInApi = new PaymentInApi(new DefaultHttpService());

  public getPaymentIn(id: number): Promise<any> {
    return this.paymentInApi.get(id);
  }

  public updatePaymentIn(id: number, dateBanked: string, administrationCenterId: number): Promise<any> {
    return this.paymentInApi.update(id, { dateBanked, administrationCenterId });
  }

  public reverse(id: number) {
    return this.paymentInApi.reverse(id);
  }

  public getPerformedPaymentInfo(merchantReference: string): Promise<PaymentIn> {
    return this.paymentInApi.getPerformedPaymentInfo(merchantReference);
  }
}

export default new PaymentInService();
