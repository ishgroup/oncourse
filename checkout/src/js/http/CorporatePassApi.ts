import {HttpService} from "../common/services/HttpService";
import {CorporatePass, GetCorporatePassRequest, MakeCorporatePassRequest} from "../model";

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
