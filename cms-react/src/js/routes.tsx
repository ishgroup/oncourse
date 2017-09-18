import React from 'react';
import {Content} from './containers/content/Content';
import {Blocks} from './containers/blocks/Blocks';

import Pages from './containers/pages/Pages';
import PagesSidebar from './containers/pages/containers/PagesSidebar';

import {Design} from './containers/design/Design';
import Menus from './containers/menus/Menus';
import Login from "./containers/auth/Login";

export interface Route {
  title?: string;
  path: string;
  exact?: boolean;
  isPublic?: boolean;
  icon?: string;
  main: (props) => any;
  sidebar?: () => any;
  routes?: Route[];
}

export const routes: Route[] = [
  {
    title: 'Content',
    path: '/',
    exact: true,
    icon: 'icon-airplay',
    main: () => <Content/>,
  },
  {
    title: 'Blocks',
    path: '/blocks',
    icon: 'icon-dashboard',
    main: () =>  <Blocks/>,
  },
  {
    title: 'Pages',
    path: '/pages',
    icon: 'icon-content_copy',
    main: props =>  <Pages {...props}/>,
    sidebar: () => <PagesSidebar/>,
    routes: [{
      title: 'Pages',
      path: '/pages/:id',
      main: () =>  <Pages/>,
      sidebar: () => <PagesSidebar/>,
    }],
  },
  {
    title: 'Menus',
    path: '/menus',
    icon: 'icon-menu',
    main: () =>  <Menus/>,
  },
  {
    title: 'Design',
    path: '/design',
    icon: 'icon-photo_album',
    main: () =>  <Design/>,
  },
  {
    title: 'Login',
    path: '/login',
    isPublic: true,
    main: () => <Login/>,
  },
];

