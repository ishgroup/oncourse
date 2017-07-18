import {HttpService} from "../common/services/HttpService";
import {CorporatePass} from "../model/checkout/corporatepass/CorporatePass";
import {GetCorporatePassRequest} from "../model/checkout/corporatepass/GetCorporatePassRequest";
import {MakeCorporatePassRequest} from "../model/checkout/corporatepass/MakeCorporatePassRequest";
import {CommonError} from "../model/common/CommonError";
import {ValidationError} from "../model/common/ValidationError";

export class CorporatePassApi {
  constructor(private http: HttpService) {
  }

  getCorporatePass(request: GetCorporatePassRequest): Promise<CorporatePass> {
    return this.http.POST(`/getCorporatePass`, request);
  }
  isCorporatePassEnabled(): Promise<boolean> {
    return this.http.GET(`/isCorporatePassEnabled`);
  }
  makeCorporatePass(request: MakeCorporatePassRequest): Promise<any> {
    return this.http.POST(`/makeCorporatePass`, request);
  }
}
