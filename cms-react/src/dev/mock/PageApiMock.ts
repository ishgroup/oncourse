import {promiseReject, promiseResolve} from "./MockAdapter";
import {API} from "../../js/constants/Config";

export function pageApiMock(mock) {
  mock.onGet(API.GET_PAGES).reply(config => promiseResolve(
    config,
    [
      {
        id: 1,
        title: 'Page - 1',
        visible: true,
        theme: 'first',
        layout: 'custom',
        url: '/page1',
        html: "<div>\n  <h1>Page Html 1</h1>\n  <p>Page text 1</p>\n</div>",
      },
      {
        id: 2,
        title: 'Page - 2',
        visible: true,
        theme: 'first',
        layout: 'custom',
        url: '/page2',
        html: "<div>\n  <h2>Page Html 2</h2>\n  <p>\n    <small>Page text 2</small>\n  </p>\n  <p>\n    Lorem ipsum dolor sit amet, consectetur adipisicing elit. \n    Accusantium adipisci autem commodi culpa cupiditate distinctio dolore doloremque \n    eius eveniet exercitationem facere facilis fuga fugit illo illum iste magnam \n    maxime minima nam nemo numquam officia provident quas quidem reprehenderit \n    repudiandae rerum sed totam ullam unde, velit vero vitae voluptate? Error, \n    soluta.\n  </p>\n</div>\n",
      },
      {
        id: 3,
        title: 'Page - 3',
        visible: false,
        theme: 'first',
        layout: 'custom',
        url: '/page3',
        html: "<div>\n  <h4>Page Html 3</h4>\n  <p>Page text 3</p>\n  <p>Other Page text 3</p>\n  <p>\n    Lorem ipsum dolor sit amet, consectetur adipisicing elit. \n  Beatae distinctio doloremque illum iure neque nisi perspiciatis quas quasi \n  repudiandae sed?\n  </p>\n</div>\n",
      },
    ],
  ));

  mock.onPost(API.SAVE_PAGE).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    return promiseResolve(
      config,
      JSON.parse(config.data),
    );
  });


  mock.onPost(API.DELETE_PAGE).reply(config => {
    return promiseResolve(
      config,
      null,
    );
  });
}
