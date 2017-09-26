import {PageApi} from "../http/PageApi";
import {Page} from "../model";
import {State} from "../reducers/state";

class PageService {
  readonly pageApi = new PageApi();

  public getPages(): Promise<Page[]> {
    return this.pageApi.getPages();
  }

  public savePage(props, state: State): Promise<Page[]> {
    return this.pageApi.savePage(this.buildSavePageRequest(props, state));
  }

  public deletePage(id): Promise<Page[]> {
    return this.pageApi.deletePage(id);
  }

  public buildSavePageRequest(props, state: State) {
    const page = state.page.items.find(p => p.id === props.id);
    return {
      ...page,
      ...props,
    };
  }

}

export default new PageService();
