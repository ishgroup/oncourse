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
        html: "<div class='editor-area'><h1>Page Html 1</h1><p>Page text 1</p></div>",
      },
      {
        id: 2,
        title: 'Page - 2',
        visible: true,
        theme: 'first',
        layout: 'custom',
        url: '/page2',
        html: "<div class='editor-area'><h2>Page Html 2</h2><p><small>Page text 2</small></p><p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Accusantium adipisci autem commodi culpa cupiditate distinctio dolore doloremque eius eveniet exercitationem facere facilis fuga fugit illo illum iste magnam maxime minima nam nemo numquam officia provident quas quidem reprehenderit repudiandae rerum sed totam ullam unde, velit vero vitae voluptate? Error, soluta.</p></div>",
      },
      {
        id: 3,
        title: 'Page - 3',
        visible: false,
        theme: 'first',
        layout: 'custom',
        url: '/page3',
        html: "<div class='editor-area'><h4>Page Html 3</h4><p>Page text 3</p><p>Other Page text 3</p><p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Beatae distinctio doloremque illum iure neque nisi perspiciatis quas quasi repudiandae sed?</p></div>",
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
