import { IntegrationApi, Integration } from "@api/model";
import { DefaultHttpService } from "../../../../../common/services/HttpService";

class IntegrationService {
  readonly integrationApi = new IntegrationApi(new DefaultHttpService());

  public getMyobIntegrationAuthUrl(): Promise<any> {
    return this.integrationApi.getMyobAuthUrl();
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
}

export default new IntegrationService();
