import {Version, VersionStatus} from "../model";
import {VersionApi} from "../../../build/generated-sources";
import {DefaultHttpService} from "../common/services/HttpService";

class PublishService {
  readonly versionApi = new VersionApi(new DefaultHttpService());

  public getVersions(): Promise<Version[]> {
    return this.versionApi.getVersions();
  }

  public setVersion(id, status: VersionStatus): Promise<any> {
    return this.versionApi.updateVersion(id, {status: status});
  }
}

export default new PublishService();
