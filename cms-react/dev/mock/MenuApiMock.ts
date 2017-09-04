import {promiseResolve} from "./MockAdapter";

export function menuApiMock(mock) {
  mock.onGet('/getMenuItems').reply(config => promiseResolve(
    config,
    [
      {
        id: 1,
        title: 'Menu - 1',
        expanded: true,
        url: '/menu1',
        children: [
          {
            id: 4,
            title: 'Menu 1 - 1',
            url: '/menu1-1',
            expanded: true,
            children: [
              {
                id: 5,
                title: 'Sub menu 1 - 1 - 1',
                url: '/menu1-1-1',
              },
            ],
          },
        ],
      },
      {
        id: 2,
        title: 'Menu - 2',
        expanded: true,
        url: '/menu2',
        children: [
          {
            id: 6,
            title: 'Menu 2 - 1',
            url: '/menu2-1',
            expanded: true,
            children: [
              {
                id: 7,
                title: 'Sub menu 2 - 1',
                url: '/menu 2-1-2',
              },
            ],
          },
        ],
      },
      {
        id: 3,
        title: 'Menu - 3',
        expanded: true,
        url: '/menu3',
      },
    ],
  ));
}
