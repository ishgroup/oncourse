import {HttpService} from "../common/services/HttpService";
import {CodeResponse} from "../model/checkout/CodeResponse";
import {Promotion} from "../model/web/Promotion";

export class PromotionApi {
  constructor(private http: HttpService) {
  }

  getPromotion(code: string): Promise<Promotion> {
    return this.http.GET(`/promotion/${code}`);
  }
  submitCode(code: string): Promise<CodeResponse> {
    return this.http.GET(`/submitCode/${code}`);
  }
}
