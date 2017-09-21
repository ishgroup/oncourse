import {PageApi} from "../http/PageApi";
import {Page} from "../model";

class PageService {
  readonly pageApi = new PageApi();

  public getPages(): Promise<Page[]> {
    return this.pageApi.getPages();
  }

  public savePageSettings(payload): Promise<Page[]> {
    return this.pageApi.savePageSettings(payload);
  }

  public savePageHtml(payload): Promise<Page[]> {
    return this.pageApi.savePageHtml(payload);
  }
}

export default new PageService();
