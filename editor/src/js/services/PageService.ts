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

  public getPageRender(request: {id: number}): Promise<any> {
    return this.pageApi.getPageRender(request);
  }

  public isValidPageUrl(url, pages) {

  }

  public buildSavePageRequest(props, state: State): SavePageRequest {
    const page = state.page.items.find(p => p.id === props.id);
    const request: SavePageRequest = new SavePageRequest();
    const newPage = {...page, ...props};

    request.id = newPage.id;
    request.title = newPage.title;
    request.theme = newPage.theme;
    request.html = newPage.html;
    request.layout = newPage.layout;
    request.urls = newPage.urls;
    request.visible = newPage.visible;

    return request;
  }

}

export default new PageService();
