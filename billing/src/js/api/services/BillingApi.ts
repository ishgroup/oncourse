import { DefaultHttpService } from "./HttpService";
import { BillingApi, CollegeDTO } from "@api/model";
import {Site} from "../../models/Site";


class BillingService {
  readonly defaultHttpService = new DefaultHttpService();
  readonly billingApi = new BillingApi(this.defaultHttpService);

  public verifyCollegeName(name: string, xGRecaptcha?: string): Promise<boolean> {
    // return this.billingApi.verifyCollegeName(name, xGRecaptcha);
    return this.defaultHttpService.GET(`/v1/college/${name}`, { headers: { xGRecaptcha,  } });
  }

  public checkUser(user: string): Promise<boolean> {
    return this.defaultHttpService.GET(`/v1/user/${user}`);
  }

  public getSites(userId: string): Promise<Site[]> {
    return this.defaultHttpService.GET(`/v1/sites/${userId}`);
  }

  public createCollege(data: CollegeDTO): Promise<any> {
    return this.billingApi.createCollege(data);
  }
}

export default new BillingService();
