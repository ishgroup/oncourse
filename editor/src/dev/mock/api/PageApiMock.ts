import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function pageApiMock() {
  this.api.onGet(API.PAGE).reply(config => {

    const url = config.params.pageUrl;
    const page = this.db.getPageByUrl(url);

    if (url && url === '/courses') {
      return promiseReject(config, {message: 'Reserved page'}, 403);
    }

    return promiseResolve(
      config,
      url ? [page] : this.db.pages,
    );
  });

  this.api.onPut(API.PAGE).reply(config => {
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

  this.api.onPost(API.PAGE).reply(config => {
    return promiseResolve(
      config,
      this.db.createNewPage(),
    );
  });


  this.api.onDelete(API.PAGE_DELETE).reply(config => {

    console.log(config);
    const id = config.url.split('/')[1];
    this.db.deletePageById(id);

    return promiseResolve(
      config,
      null,
    );
  });
  
}
