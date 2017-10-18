import {HttpService} from "../common/services/HttpService";
import {CorporatePass, GetCorporatePassRequest, MakeCorporatePassRequest} from "../model";
import {CheckoutModelRequest} from "../model/checkout/CheckoutModelRequest";

export class CorporatePassApi {
  constructor(private http: HttpService) {
  }

  getCorporatePass(request: GetCorporatePassRequest): Promise<CorporatePass> {
    return this.http.POST(`/getCorporatePass`, request);
  }

  isCorporatePassEnabledFor(checkoutModelRequest: CheckoutModelRequest): Promise<boolean> {
    return this.http.POST(`/isCorporatePassEnabledFor`, checkoutModelRequest);
  }

  makeCorporatePass(request: MakeCorporatePassRequest): Promise<any> {
    return this.http.POST(`/makeCorporatePass`, request);
  }
}
