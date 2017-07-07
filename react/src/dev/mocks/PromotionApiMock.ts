import {PromotionApi} from "../../js/http/PromotionApi";
import {Promotion} from "../../js/model/web/Promotion";
import {MockConfig} from "./mocks/MockConfig";

export class PromotionApiMock extends PromotionApi {
  public config: MockConfig;

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

  submitCode(code: string): Promise<Promotion> {
    const voucherMock = {
      name: `${code} voucher`,
      id: `${code}-100`,
      enabled: true,
      payer: this.config.db.contacts.entities.contact[this.config.db.contacts.result[code]],
    };
    const promotionMock = {
      code,
      id: new Date().getTime().toString(),
      name: code,
    };

    return code === '1' || code === '2' ? Promise.resolve({
      voucher: voucherMock,
      promotion: null,
    }) : Promise.resolve({
      voucher: null,
      promotion: promotionMock,
    });
  }
}
