import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class HistoryApi {
  private http = new DefaultHttpService();

  getHistory(): Promise<any> {
    return this.http.GET(API.GET_HISTORY);
  }
}
