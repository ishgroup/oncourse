import {CorporatePassApi} from "../../js/http/CorporatePassApi";
import {CreatePromiseReject, MockConfig} from "./mocks/MockConfig";
import {
  GetCorporatePassRequest, CorporatePass, MakeCorporatePassRequest,
} from "../../js/model";

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
      : CreatePromiseReject(this.config.createValidationError(1, ['corporatePass']));
  }
  isCorporatePassEnabled(): Promise<boolean> {
    return this.config.createResponse(true);
  }
  makeCorporatePass(request: MakeCorporatePassRequest): Promise<any> {
    return this.config.createResponse('');
  }
}
