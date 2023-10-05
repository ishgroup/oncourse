import { Integration, IntegrationApi } from "@api/model";
import { DefaultHttpService } from "../../../../../common/services/HttpService";

class IntegrationService {
  readonly integrationApi = new IntegrationApi(new DefaultHttpService());

  public getMyobIntegrationAuthUrl(): Promise<any>{
    return Promise.any("https://secure.myob.com/oauth2/account/authorize?client_id=07545d14-9a95-4398-b907-75af3b3841ae\n" +
      "&redirect_uri=http%3A%2F%2Fdesktop&response_type=code&scope=CompanyFile");
  }

  public getIntegrations(): Promise<any> {
    return this.integrationApi.get();
  }

  public createIntegration(item: Integration): Promise<any> {
    return this.integrationApi.create(item);
  }

  public updateIntegration(id: string, item: Integration): Promise<any> {
    return this.integrationApi.update(id, item);
  }

  public deleteIntegrationItem(id: string): Promise<any> {
    return this.integrationApi.remove(id);
  }

  public getSsoPluginTypes(): Promise<number[]> {
    return this.integrationApi.getSsoPluginTypes();
  }
}

export default new IntegrationService();
