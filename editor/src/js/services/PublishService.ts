import {Version} from "../model";
import {VersionApi} from "../http/VersionApi";
import {DefaultHttpService} from "../common/services/HttpService";

class PublishService {
  readonly versionApi = new VersionApi(new DefaultHttpService());

  public getVersions(): Promise<Version[]> {
    return this.versionApi.versionListGet();
  }

  public setVersion(id): Promise<any> {
    return this.versionApi.versionDraftSetIdPost(id);
  }

  public publish(): Promise<any> {
    return this.versionApi.versionDraftPublishPost();
  }
}

export default new PublishService();
