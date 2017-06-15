import {PromotionApi} from "../../js/http/PromotionApi";
import {Promotion} from "../../js/model/web/Promotion";

export class PromotionApiMock extends PromotionApi {
  getPromotion(code: string): Promise<Promotion> {
    return Promise.resolve({
      code,
      id: new Date().getTime().toString(),
      name: code,
    });
  }
}
