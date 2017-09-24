import {PageApi} from "../http/PageApi";
import {Page} from "../model";

class PageService {
  readonly pageApi = new PageApi();

  public getPages(): Promise<Page[]> {
    return this.pageApi.getPages();
  }

  public savePage(props, state): Promise<Page[]> {
    return this.pageApi.savePage(this.buildSavePageRequest(props, state));
  }

  public buildSavePageRequest(props, state) {
    const page = state.page.pages.find(p => p.id === props.id);
    return {
      ...page,
      ...props,
    };
  }

}

export default new PageService();
