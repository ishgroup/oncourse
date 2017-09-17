import {PageApi} from "../http/PageApi";
import {Page} from "../model";

class PageService {
  readonly pageApi = new PageApi();

  public getPages(): Promise<Page[]> {
    return this.pageApi.getPages();
  }
}

export default new PageService();
