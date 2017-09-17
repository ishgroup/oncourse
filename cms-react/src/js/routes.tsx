import React from 'react';
import {Content} from './containers/content/Content';
import {Blocks} from './containers/blocks/Blocks';
import {Pages} from './containers/pages/Pages';
import {Design} from './containers/design/Design';
import Menus from './containers/menus/Menus';
import Login from "./containers/auth/Login";

export interface Route {
  title?: string;
  path: string;
  exact?: boolean;
  isPublic?: boolean;
  icon?: string;
  main: () => any;
}

export const routes = [
  {
    title: 'Content',
    path: '/',
    exact: true,
    isPublic: false,
    icon: 'icon-airplay',
    main: () => <Content/>,
  },
  {
    title: 'Blocks',
    path: '/blocks',
    exact: false,
    isPublic: false,
    icon: 'icon-dashboard',
    main: () =>  <Blocks/>,
  },
  {
    title: 'Pages',
    path: '/pages',
    exact: false,
    isPublic: false,
    icon: 'icon-content_copy',
    main: () =>  <Pages/>,
  },
  {
    title: 'Menus',
    path: '/menus',
    exact: false,
    isPublic: false,
    icon: 'icon-menu',
    main: () =>  <Menus/>,
  },
  {
    title: 'Design',
    path: '/design',
    exact: false,
    isPublic: false,
    icon: 'icon-photo_album',
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

