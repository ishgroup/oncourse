import {HttpService} from "../common/services/HttpService";
import {Version} from "../model/Version";
import {CommonError} from "../model/common/CommonError";

export class VersionApi {
  constructor(private http: HttpService) {
  }

  getVersions(): Promise<Version[]> {
    return this.http.GET(`/version.list`);
  }
  publish(): Promise<any> {
    return this.http.POST(`/version.draft.publish`);
  }
  setVersion(id: string): Promise<any> {
    return this.http.POST(`/version.draft.set/${id}`);
  }
}
