import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class PageApi {
  private http = new DefaultHttpService();

  getPages(): Promise<any> {
    return this.http.GET(API.GET_PAGES);
  }

  savePage(payload): Promise<any> {
    return this.http.POST(API.SAVE_PAGE, payload);
  }
}
