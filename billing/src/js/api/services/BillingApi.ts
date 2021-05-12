import { DefaultHttpService } from "./HttpService";
import { BillingApi, CollegeDTO } from "@api/model";


class BillingService {
  readonly defaultHttpService = new DefaultHttpService();
  readonly billingApi = new BillingApi(this.defaultHttpService);

  public verifyCollegeName(name: string, xGRecaptcha?: string): Promise<any> {
    // return this.billingApi.verifyCollegeName(name, xGRecaptcha);
    return this.defaultHttpService.GET(`/v1/college/${name}`, { headers: { xGRecaptcha,  } });
  }

  public createCollege(data: CollegeDTO): Promise<any> {
    return this.billingApi.createCollege(data);
  }
}

export default new BillingService();
