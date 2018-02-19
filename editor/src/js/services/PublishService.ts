import {Version} from "../model";
import {VersionApi} from "../http/VersionApi";
import {DefaultHttpService} from "../common/services/HttpService";

class PublishService {
  readonly versionApi = new VersionApi(new DefaultHttpService());

  public getVersions(): Promise<Version[]> {
    return this.versionApi.getVersions();
  }

  public setVersion(id): Promise<any> {
    return this.versionApi.setVersion(id);
  }

  public publish(): Promise<any> {
    return this.versionApi.publish();
  }
}

export default new PublishService();
