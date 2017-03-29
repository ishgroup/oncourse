import {PromotionApi} from "../http/PromotionApi";
import {Promotion} from "../model/Promotion";

export class PromotionApiStub extends PromotionApi {
  getPromotion(code: string): Promise<Promotion> {
    return Promise.resolve({
      id: new Date().getTime().toString(),
      name: code,
      code: code
    });
  }
}
