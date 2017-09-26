import {promiseReject, promiseResolve} from "./MockAdapter";
import {API} from "../../js/constants/Config";

export function blockApiMock(mock) {
  mock.onGet(API.GET_BLOCKS).reply(config => promiseResolve(
    config,
    [
      {
        id: 1,
        title: 'Header',
        html: "<div><h1>Header Title</h1></div>",
      },
      {
        id: 2,
        title: 'Footer',
        html: "<div><footer>Lorem ipsum dolor sit amet.</footer></div>",
      },
      {
        id: 3,
        title: 'Content',
        html: "<div><ul><li>Lorem ipsum dolor sit amet.</li><li>Lorem ipsum.</li><li>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Suscipit!</li></ul></div>",
      },
    ],
  ));

  mock.onPost(API.SAVE_BLOCK).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    return promiseResolve(
      config,
      JSON.parse(config.data),
    );
  });


  mock.onPost(API.DELETE_BLOCK).reply(config => {
    return promiseResolve(
      config,
      null,
    );
  });
}
