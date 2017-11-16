import {HttpService} from "../common/services/HttpService";
import {Page} from "../model/Page";
import {PageRenderResponse} from "../model/api/PageRenderResponse";
import {CommonError} from "../model/common/CommonError";

export class PageApi {
  constructor(private http: HttpService) {
  }

  addPage(): Promise<CommonError> {
    return this.http.POST(`/addPage`);
  }
  deletePage(id: number): Promise<any> {
    return this.http.POST(`/deletePage`, id);
  }
  getPageByUrl(pageUrl: string): Promise<Page> {
    return this.http.GET(`/getPageByUrl`, { params: { pageUrl }});
  }
  getPageRender(pageId: number): Promise<PageRenderResponse> {
    return this.http.GET(`/getPageRender/${pageId}`);
  }
  getPages(): Promise<Page[]> {
    return this.http.GET(`/getPages`);
  }
  savePage(pageParams: Page): Promise<Page> {
    return this.http.POST(`/savePage`, pageParams);
  }
}
