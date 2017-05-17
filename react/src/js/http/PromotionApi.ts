import {HttpService} from "../common/services/HttpService";
import { CommonError } from "../model/common/CommonError";
import { Promotion } from "../model/web/Promotion";
import { PromotionNotFound } from "../model/web/PromotionNotFound";

export class PromotionApi {
  constructor(private http: HttpService) {
  }

  getPromotion(code: string): Promise<Promotion> {
    return this.http.GET(`/promotion/${code}`)
  }
}
