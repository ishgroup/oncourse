import {HttpService} from "../common/services/HttpService";
import { ModelError } from "../model/ModelError";
import { Promotion } from "../model/Promotion";
import { PromotionNotFound } from "../model/PromotionNotFound";

export class PromotionApi {
  constructor(private http: HttpService) {
  }

  getPromotion(code: string): Promise<Promotion> {
    return this.http.GET(`/promotion/${code}`)
  }
}
