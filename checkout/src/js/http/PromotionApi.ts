import {HttpService} from "../common/services/HttpService";
import {CodeResponse, Promotion} from "../model";

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
