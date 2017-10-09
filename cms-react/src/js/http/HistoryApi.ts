import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class HistoryApi {
  private http = new DefaultHttpService();

  getVersions(): Promise<any> {
    return this.http.GET(API.GET_VERSIONS);
  }
}
