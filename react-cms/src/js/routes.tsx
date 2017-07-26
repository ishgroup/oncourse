import React from 'react';
import {Content} from './containers/Content/Content';
import {Blocks} from './containers/Blocks/Blocks';
import {Pages} from './containers/Pages/Pages';
import {Design} from './containers/Design/Design';
import {Menus} from './containers/Menus/Menus';

export const routes = [
  {
    title: 'Content',
    path: '/',
    exact: true,
    main: () => <Content/>,
  },
  {
    title: 'Blocks',
    path: '/blocks',
    exact: false,
    main: () =>  <Blocks/>,
  },
  {
    title: 'Pages',
    path: '/pages',
    exact: false,
    main: () =>  <Pages/>,
  },
  {
    title: 'Menus',
    path: '/menus',
    exact: false,
    main: () =>  <Menus/>,
  },
  {
    title: 'Design',
    path: '/design',
    exact: false,
    main: () =>  <Design/>,
  },
];

