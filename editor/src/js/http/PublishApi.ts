import {HttpService} from "../common/services/HttpService";
import {Version} from "../model/Version";
import {SetVersionRequest} from "../model/api/SetVersionRequest";
import {CommonError} from "../model/common/CommonError";

export class PublishApi {
  constructor(private http: HttpService) {
  }

  getVersions(): Promise<Version[]> {
    return this.http.GET(`/getVersions`);
  }
  publish(): Promise<any> {
    return this.http.POST(`/publish`);
  }
  setVersion(setVersionRequest: SetVersionRequest): Promise<any> {
    return this.http.POST(`/setVersion`, setVersionRequest);
  }
}
