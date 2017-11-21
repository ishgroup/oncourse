import {PageApi} from "../http/PageApi";
import {Page} from "../model";
import {State} from "../reducers/state";
import {DefaultHttpService} from "../common/services/HttpService";

class PageService {
  readonly pageApi = new PageApi(new DefaultHttpService());

  public getPages(): Promise<Page[]> {
    return this.pageApi.getPages();
  }

  public getPageByUrl(url: string): Promise<Page> {
    return this.pageApi.getPageByUrl(url);
  }

  public savePage(props, state: State): Promise<any> {
    return this.pageApi.savePage(this.buildSavePageRequest(props, state));
  }

  public addPage(): Promise<any> {
    return this.pageApi.addPage();
  }

  public deletePage(id: number): Promise<any> {
    return this.pageApi.deletePage(id);
  }

  public getPageRender(id: number): Promise<any> {
    return this.pageApi.getPageRender(id);
  }

  public isValidPageUrl(link, pages) {
    return link && !pages.find(page => page.urls.find(i => i.link === link));
  }

  public generateBasetUrl(page) {
    return {
      link: `/page/${page.id}`,
    };
  }

  public buildSavePageRequest(props, state: State): Page {
    const page = state.page.items.find(p => p.id === props.id);
    const request: Page = new Page();
    const newPage: Page = {...page, ...props};

    request.id = newPage.id;
    request.title = newPage.title;
    request.themeId = newPage.themeId;
    request.content = newPage.content;
    request.urls = newPage.urls;
    request.visible = newPage.visible;

    return request;
  }

}

export default new PageService();
