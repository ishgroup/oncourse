import { DefaultHttpService } from "./HttpService";
import {BillingApi, CollegeDTO, SiteDTO} from "@api/model";


class BillingService {
  readonly defaultHttpService = new DefaultHttpService();
  readonly billingApi = new BillingApi(this.defaultHttpService);

  public verifyCollegeName(name: string, xGRecaptcha?: string): Promise<boolean> {
    // return this.billingApi.verifyCollegeName(name, xGRecaptcha);
    return this.defaultHttpService.GET(`/v1/college/${name}`, { headers: { xGRecaptcha,  } });
  }

  public getUser(userId: string): Promise<string> {
    return this.billingApi.getCollegeKey(userId);
  }

  public getSites(userId: string): Promise<SiteDTO[]> {
    return this.billingApi.getCollegeSites(userId);
  }

  public updateSites(sites: SiteDTO[]): Promise<any> {
    return this.billingApi.updateCollegeSites(sites);
  }

  public createCollege(data: CollegeDTO): Promise<any> {
    return this.billingApi.createCollege(data);
  }
}

export default new BillingService();
