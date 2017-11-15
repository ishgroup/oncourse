import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";
import {Version} from "../model";

export class PublishApi {
  private http = new DefaultHttpService();

  getVersions(): Promise<Version[]> {
    return this.http.GET(API.GET_VERSIONS);
  }

  setVersion(id): Promise<any> {
      return this.http.POST(API.SET_VERSION, {id});
  }

  publish(): Promise<any> {
      return this.http.POST(API.PUBLISH, null);
  }
}
