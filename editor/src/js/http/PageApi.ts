import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class PageApi {
  private http = new DefaultHttpService();

  getPages(): Promise<any> {
    return this.http.GET(API.GET_PAGES);
  }

  getPageByUrl(url): Promise<any> {
    return this.http.POST(API.GET_PAGE_BY_URL, {url});
  }

  savePage(payload): Promise<any> {
    return this.http.POST(API.SAVE_PAGE, payload);
  }

  deletePage(id): Promise<any> {
    return this.http.POST(API.DELETE_PAGE, id);
  }
}
