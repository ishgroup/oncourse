import { Application, ApplicationApi, Diff } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class ApplicationService {
  readonly applicationApi = new ApplicationApi(new DefaultHttpService());

  public bulkChange(diff: Diff): Promise<any> {
    return this.applicationApi.bulkChange(diff);
  }

  public getApplication(id: number): Promise<any> {
    return this.applicationApi.get(id);
  }

  public updateApplication(id: number, application: Application): Promise<any> {
    return this.applicationApi.update(id, application);
  }

  public createApplication(application: Application): Promise<any> {
    return this.applicationApi.create(application);
  }

  public removeApplication(id: number): Promise<any> {
    return this.applicationApi.remove(id);
  }
}

export default new ApplicationService();
