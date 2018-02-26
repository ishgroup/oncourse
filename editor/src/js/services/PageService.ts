import {PageApi} from "../../../build/generated-sources";
import axios from "axios";
import {Page} from "../model";
import {State} from "../reducers/state";
import {DefaultHttpService} from "../common/services/HttpService";

class PageService {
  readonly pageApi = new PageApi(new DefaultHttpService());

  public getPages(url?): Promise<Page[]> {
    return this.pageApi.getPages(url ? url : null);
  }

  public savePage(props, state: State): Promise<any> {
    return this.pageApi.updatePage(this.buildSavePageRequest(props, state));
  }

  public addPage(): Promise<any> {
    return this.pageApi.createPage();
  }

  public deletePage(id: number): Promise<any> {
    return this.pageApi.deletePage(id.toString());
  }

  public getPageRender(id: number): Promise<any> {
    const instance = axios.create();
    return instance.get(`/page/${id}`).then(
      payload => {
        const template = document.createElement('div');
        template.innerHTML = payload.data;
        const content = template.querySelector("div[class^='block-']").innerHTML;
        return {html: content};
      },
      payload => Promise.reject(payload.response),
    ) as Promise<any>;
  }

  public isValidPageUrl(link, pages) {
    return link && !pages.find(page => page.urls.find(i => i.link === link));
  }

  public generateBasetUrl(page) {
    return {
      link: `/page/${page.serialNumber}`,
    };
  }

  public buildSavePageRequest(props, state: State): Page {
    const page = state.page.items.find(p => p.serialNumber === props.serialNumber);
    const request: Page = {} as Page;
    const newPage: Page = {...page, ...props};

    request.serialNumber = newPage.serialNumber;
    request.id = newPage.id;
    request.title = newPage.title;
    request.themeId = newPage.themeId;
    request.content = newPage.content;
    request.urls = newPage.urls;
    request.visible = newPage.visible;
    request.suppressOnSitemap = newPage.suppressOnSitemap;

    return request;
  }

}

export default new PageService();
