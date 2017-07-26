import React from 'react';
import {Content} from './containers/Content/Content';
import {Blocks} from './containers/Blocks/Blocks';
import {Pages} from './containers/Pages/Pages';
import {Design} from './containers/Design/Design';
import {Menus} from './containers/Menus/Menus';
import Login from "./containers/Login/Login";

export const routes = [
  {
    title: 'Content',
    path: '/',
    exact: true,
    isPublic: false,
    main: () => <Content/>,
  },
  {
    title: 'Blocks',
    path: '/blocks',
    exact: false,
    isPublic: false,
    main: () =>  <Blocks/>,
  },
  {
    title: 'Pages',
    path: '/pages',
    exact: false,
    isPublic: false,
    main: () =>  <Pages/>,
  },
  {
    title: 'Menus',
    path: '/menus',
    exact: false,
    isPublic: false,
    main: () =>  <Menus/>,
  },
  {
    title: 'Design',
    path: '/design',
    exact: false,
    isPublic: false,
    main: () =>  <Design/>,
  },
  {
    title: 'Login',
    path: '/login',
    exact: false,
    isPublic: true,
    main: () => <Login/>,
  },
];

