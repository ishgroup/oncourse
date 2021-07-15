import { BillingApi, CollegeDTO } from '@api/model';
import { DefaultHttpService } from './HttpService';

class BillingService {
  readonly defaultHttpService = new DefaultHttpService();

  readonly billingApi = new BillingApi(this.defaultHttpService);

  public verifyCollegeName(name: string, xGRecaptcha?: string): Promise<boolean> {
    // return this.billingApi.verifyCollegeName(name, xGRecaptcha);
    return this.defaultHttpService.GET(`/v1/college/${name}`, { headers: { xGRecaptcha } });
  }

  public getCollegeKey(): Promise<string> {
    return this.billingApi.getCollegeKey();
  }

  public createCollege(data: CollegeDTO): Promise<any> {
    return this.billingApi.createCollege(data);
  }
}

export default new BillingService();
