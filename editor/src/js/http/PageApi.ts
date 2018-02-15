import {HttpService} from "../common/services/HttpService";
import {Page} from "../model/Page";
import {CommonError} from "../model/common/CommonError";

export class PageApi {
  constructor(private http: HttpService) {
  }

  addPage(): Promise<Page> {
    return this.http.POST(`/addPage`);
  }
  deletePage(id: string): Promise<any> {
    return this.http.POST(`/deletePage/${id}`);
  }
  getPageByUrl(pageUrl: string): Promise<Page> {
    return this.http.GET(`/getPageByUrl`, { params: { pageUrl }});
  }
  getPages(): Promise<Page[]> {
    return this.http.GET(`/getPages`);
  }
  savePage(pageParams: Page): Promise<Page> {
    return this.http.POST(`/savePage`, pageParams);
  }
}
