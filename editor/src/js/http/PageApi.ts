import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";
import {Page, SavePageRequest} from "../model";

export class PageApi {
  private http = new DefaultHttpService();

  getPages(): Promise<Page[]> {
    return this.http.GET(API.GET_PAGES);
  }

  getPageByUrl(url): Promise<Page> {
    return this.http.POST(API.GET_PAGE_BY_URL, {url});
  }

  savePage(payload: SavePageRequest): Promise<any> {
    return this.http.POST(API.SAVE_PAGE, payload);
  }

  addPage(): Promise<any> {
    return this.http.POST(API.ADD_PAGE);
  }

  deletePage(id): Promise<any> {
    return this.http.POST(API.DELETE_PAGE, id);
  }

  getPageRender(request): Promise<any> {
    return this.http.POST(API.GET_PAGE_RENDER, request);
  }

}
