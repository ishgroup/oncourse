import React from 'react';
import {Content} from './containers/content/Content';

import Blocks from './containers/blocks/Blocks';
import BlockSidebar from './containers/blocks/containers/BlockSidebar';

import Pages from './containers/pages/Pages';
import PagesSidebar from './containers/pages/containers/PagesSidebar';

import Themes from './containers/themes/Themes';
import ThemesSidebar from "./containers/themes/containers/ThemesSidebar";

import Menus from './containers/menus/Menus';
import Login from "./containers/auth/Login";

export const URL = {
  CONTENT: '/',
  BLOCKS: '/blocks',
  PAGES: '/pages',
  MENUS: '/menus',
  THEMES: '/themes',
  LOGIN: '/login',
};

export interface Route {
  title?: string;
  path: string;
  url?: string;
  exact?: boolean;
  isPublic?: boolean;
  icon?: string;
  main: (props) => any;
  sidebar?: (props?) => any;
  routes?: Route[];
}

export const routes: Route[] = [
  {
    title: 'Content',
    path: '/',
    url: URL.CONTENT,
    exact: true,
    icon: 'icon-airplay',
    main: props => <Content props/>,
  },
  {
    title: 'Blocks',
    path: '/blocks/:id?',
    url: URL.BLOCKS,
    icon: 'icon-dashboard',
    main: props => <Blocks {...props}/>,
    sidebar: props => <BlockSidebar {...props}/>,
  },
  {
    title: 'Pages',
    path: '/pages/:id?',
    url: URL.PAGES,
    icon: 'icon-content_copy',
    main: props => <Pages {...props}/>,
    sidebar: props => <PagesSidebar {...props}/>,
  },
  {
    title: 'Menus',
    path: '/menus',
    url: URL.MENUS,
    icon: 'icon-menu',
    main: props => <Menus {...props}/>,
  },
  {
    title: 'Themes',
    path: '/themes/:id?',
    url: URL.THEMES,
    icon: 'icon-photo_album',
    main: props => <Themes {...props}/>,
    sidebar: props => <ThemesSidebar {...props}/>,
  },
  {
    title: 'Login',
    path: '/login',
    url: URL.LOGIN,
    isPublic: true,
    main: () => <Login/>,
  },
];

