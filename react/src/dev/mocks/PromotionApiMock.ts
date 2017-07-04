import {PromotionApi} from "../../js/http/PromotionApi";
import {Promotion} from "../../js/model/web/Promotion";

export class PromotionApiMock extends PromotionApi {
  submitCode(code: string): Promise<Promotion> {
    const voucherMock = {
      name: '50% voucher',
      id: '200',
      enabled: true,
    };
    const promotionMock = {
      code,
      id: new Date().getTime().toString(),
      name: code,
    };

    return code === '1' ? Promise.resolve({
      voucher: voucherMock,
      promotion: null,
    }) : Promise.resolve({
      voucher: null,
      promotion: promotionMock,
    });
  }
}
