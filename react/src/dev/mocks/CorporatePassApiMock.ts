import {CorporatePassApi} from "../../js/http/CorporatePassApi";
import {CreatePromiseReject, MockConfig} from "./mocks/MockConfig";
import {GetCorporatePassRequest} from "../../js/model/checkout/corporatepass/GetCorporatePassRequest";
import {CorporatePass} from "../../js/model/checkout/corporatepass/CorporatePass";
import {MakeCorporatePassRequest} from "../../js/model/checkout/corporatepass/MakeCorporatePassRequest";
import {PaymentStatus} from "../../js/model/checkout/payment/PaymentStatus";
import {PaymentResponse} from "../../js/model/checkout/payment/PaymentResponse";

export class CorporatePassApiMock extends CorporatePassApi {
  public config: MockConfig;

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

  getCorporatePass(request: GetCorporatePassRequest): Promise<CorporatePass> {
    const corporatePassResponse = {
      code: '1',
      message: 'Valid code entered. ' +
      'This transaction will be invoiced to SSV Normandy when you press the Confirm Purchase button below. ' +
      'Your details will be forwarded to the relevant manager at SSV Normandy. ' +
      'This corporatePass has caused a discount of $3.15 to be applied to this sale.',
      id: '1',
    };

    return request.code === '1'
      ? this.config.createResponse(corporatePassResponse)
      : CreatePromiseReject('incorrect pass');
  }
  isCorporatePassEnabled(): Promise<boolean> {
    return this.config.createResponse(true);
  }
  makeCorporatePass(request: MakeCorporatePassRequest): Promise<any> {
    const response = new PaymentResponse();
    response.status = PaymentStatus.SUCCESSFUL;
    response.sessionId = '123asjdka sjl123 asd123123';
    return this.config.createResponse(response);
  }
}
