import {HttpService} from "../common/services/HttpService";
import {Promotion} from "../model/web/Promotion";

export class PromotionApi {
  constructor(private http: HttpService) {
  }

  getPromotion(code: string): Promise<Promotion> {
    return this.http.GET(`/promotion/${code}`)
  }
}
