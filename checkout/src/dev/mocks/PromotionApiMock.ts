import {PromotionApi} from "../../js/http/PromotionApi";
import {MockConfig} from "./mocks/MockConfig";
import {CodeResponse, RedeemVoucher} from "../../js/model";

export class PromotionApiMock extends PromotionApi {
  public config: MockConfig;

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

  submitCode(code: string): Promise<CodeResponse> {
    const voucherMock: RedeemVoucher = {
      name: `${code} voucher`,
      id: `${code}-100`,
      code,
      enabled: false,
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
