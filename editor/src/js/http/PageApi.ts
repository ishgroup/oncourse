import {HttpService} from "../common/services/HttpService";
import {Page} from "../model/Page";
import {CommonError} from "../model/common/CommonError";

export class PageApi {
  constructor(private http: HttpService) {
  }

  addPage(): Promise<Page> {
    return this.http.POST(`/page.create`);
  }
  deletePage(id: string): Promise<any> {
    return this.http.POST(`/page.delete/${id}`);
  }
  getPageByUrl(pageUrl: string): Promise<Page> {
    return this.http.GET(`/page.get`, { params: { pageUrl }});
  }
  getPages(): Promise<Page[]> {
    return this.http.GET(`/page.list`);
  }
  savePage(pageParams: Page): Promise<Page> {
    return this.http.POST(`/page.update`, pageParams);
  }
}
