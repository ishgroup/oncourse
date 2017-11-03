import {PageApi} from "../http/PageApi";
import {Page, SavePageRequest} from "../model";
import {State} from "../reducers/state";

class PageService {
  readonly pageApi = new PageApi();

  public getPages(): Promise<Page[]> {
    return this.pageApi.getPages();
  }

  public getPageByUrl(url): Promise<Page> {
    return this.pageApi.getPageByUrl(url);
  }

  public savePage(props, state: State): Promise<any> {
    return this.pageApi.savePage(this.buildSavePageRequest(props, state));
  }

  public addPage(): Promise<any> {
    return this.pageApi.addPage();
  }

  public deletePage(id): Promise<any> {
    return this.pageApi.deletePage(id);
  }

  public buildSavePageRequest(props, state: State): SavePageRequest {
    const page = state.page.items.find(p => p.id === props.id);

    return {
      ...page,
      ...props,
      id: props.id != -1 ? props.id : null,
    };
  }

}

export default new PageService();
