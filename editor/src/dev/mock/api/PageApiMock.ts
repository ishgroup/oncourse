import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function pageApiMock() {
  this.api.onGet(API.GET_PAGES).reply(config => promiseResolve(
    config,
    this.db.pages,
  ));

  this.api.onGet(API.GET_PAGE_BY_URL).reply(config => {
    const url = config.params.searchText;
    const page = this.db.getPageByUrl(url);

    return promiseResolve(
      config,
      page,
    );
  });

  this.api.onPost(API.SAVE_PAGE).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    this.db.editPage(request);

    return promiseResolve(
      config,
      request,
    );
  });

  this.api.onPost(API.ADD_PAGE).reply(config => {
    return promiseResolve(
      config,
      this.db.createNewPage(),
    );
  });


  this.api.onPost(API.DELETE_PAGE).reply(config => {
    this.db.deletePageById(JSON.parse(config.data));

    return promiseResolve(
      config,
      null,
    );
  });

  this.api.onGet(API.GET_PAGE_RENDER).reply(config => {
    const id = config.params.searchText;
    const html = this.db.getPageRender(id);

    return promiseResolve(
      config,
      html,
    );
  });
}
