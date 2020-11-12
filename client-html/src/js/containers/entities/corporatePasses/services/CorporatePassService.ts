import { DefaultHttpService } from "../../../../common/services/HttpService";
import { CorporatePass, CorporatePassApi } from "@api/model";

class CorporatePassService {
  readonly corporatePassApi = new CorporatePassApi(new DefaultHttpService());

  public createCorporatePass(corporatePass: CorporatePass): Promise<any> {
    return this.corporatePassApi.create(corporatePass);
  }

  public getCorporatePass(id: number): Promise<any> {
    return this.corporatePassApi.get(id);
  }

  public updateCorporatePass(id: number, corporatePass: CorporatePass): Promise<any> {
    return this.corporatePassApi.update(id, corporatePass);
  }

  public removeCorporatePass(id: number): Promise<any> {
    return this.corporatePassApi.remove(id);
  }
}

export default new CorporatePassService();
