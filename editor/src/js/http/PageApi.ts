import {HttpService} from "../common/services/HttpService";
import {Page} from "../model/Page";
import {CommonError} from "../model/common/CommonError";

export class PageApi {
  constructor(private http: HttpService) {
  }

  pageCreatePost(): Promise<Page> {
    return this.http.POST(`/page.create`);
  }
  pageDeleteIdPost(id: string): Promise<any> {
    return this.http.POST(`/page.delete/${id}`);
  }
  pageGetGet(pageUrl: string): Promise<Page> {
    return this.http.GET(`/page.get`, { params: { pageUrl }});
  }
  pageListGet(): Promise<Page[]> {
    return this.http.GET(`/page.list`);
  }
  pageUpdatePost(pageParams: Page): Promise<Page> {
    return this.http.POST(`/page.update`, pageParams);
  }
}
