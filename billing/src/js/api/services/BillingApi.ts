import { DefaultHttpService } from "./HttpService";
import { BillingApi } from "../../../../build/generated-sources/api";
import { CollegeDTO } from "../../../../build/generated-sources/api";

class BillingService {
  readonly billingApi = new BillingApi(new DefaultHttpService());

  public verifyCollegeName(name: string, xGRecaptcha?: string): Promise<any> {
    return this.billingApi.verifyCollegeName(name, xGRecaptcha);
  }

  public createCollege(data: CollegeDTO): Promise<any> {
    return this.billingApi.createCollege(data);
  }
}

export default new BillingService();