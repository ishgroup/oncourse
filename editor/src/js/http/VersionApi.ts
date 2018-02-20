import {HttpService} from "../common/services/HttpService";
import {Version} from "../model/Version";
import {CommonError} from "../model/common/CommonError";

export class VersionApi {
  constructor(private http: HttpService) {
  }

  versionDraftPublishPost(): Promise<any> {
    return this.http.POST(`/version.draft.publish`);
  }
  versionDraftSetIdPost(id: string): Promise<any> {
    return this.http.POST(`/version.draft.set/${id}`);
  }
  versionListGet(): Promise<Version[]> {
    return this.http.GET(`/version.list`);
  }
}
