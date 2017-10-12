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

import History from './containers/history/History';

import Skills from './containers/settings/containers/Skills';
import Checkout from './containers/settings/containers/Checkout';
import Redirect from './containers/settings/containers/Redirect';
import Website from './containers/settings/containers/Website';

export const URL = {
  SITE: '/',
  CONTENT: '/content',
  BLOCKS: '/blocks',
  PAGES: '/pages',
  MENUS: '/menus',
  DESIGN: '/design',
  THEMES: '/themes',
  SETTINGS: '/settings',
  SETTINGS_SKILLS: '/settings/skills',
  SETTINGS_CHECKOUT: '/settings/checkout',
  SETTINGS_WEBSITE: '/settings/website',
  SETTINGS_REDIRECT: '/settings/redirect',
  HISTORY: '/history',
  LOGIN: '/login',
};

export interface Route {
  title?: string;                                   // route title, displayed on sidebar
  path: string;                                     // route path (regexp)
  url?: string;                                     // route link
  root?: boolean;                                   // root items displaying in main sidebar
  exact?: boolean;
  isPublic?: boolean;
  icon?: string;                                    // icon class for menu item in slim mode
  main: (props) => any;                             // main component for route
  sidebar?: (props?) => any;                        // sidebar component form route
  parent?: string;                                  // parent item url for sub menu items
}

export const routes: Route[] = [
  // Root menu items
  {
    title: 'Site',
    path: '/',
    url: URL.SITE,
    exact: true,
    root: true,
    icon: 'icon-airplay',
    main: props => <Content props/>,
  },
  {
    title: 'Content',
    root: true,
    path: '/content',
    url: URL.CONTENT,
    icon: 'icon-dashboard',
    main: props => <span/>,
  },
  {
    title: 'Settings',
    root: true,
    exact: true,
    path: '/settings',
    url: URL.SETTINGS,
    icon: 'icon-settings',
    main: props => <span/>,
  },
  {
    title: 'Design',
    root: true,
    path: '/design',
    url: URL.DESIGN,
    icon: 'icon-photo_album',
    main: props => <span/>,
  },

  // Content sub menu items
  {
    title: 'Blocks',
    path: '/blocks/:id?',
    url: URL.BLOCKS,
    parent: URL.CONTENT,
    main: props => <Blocks {...props}/>,
    sidebar: props => <BlockSidebar {...props}/>,
  },
  {
    title: 'Pages',
    path: '/pages/:id?',
    url: URL.PAGES,
    parent: URL.CONTENT,
    main: props => <Pages {...props}/>,
    sidebar: props => <PagesSidebar {...props}/>,
  },
  {
    title: 'Menus',
    path: '/menus',
    parent: URL.CONTENT,
    url: URL.MENUS,
    main: props => <Menus {...props}/>,
  },

  // Design sub menu items
  {
    title: 'Themes',
    path: '/themes/:id?',
    parent: URL.DESIGN,
    url: URL.THEMES,
    main: props => <Themes {...props}/>,
    sidebar: props => <ThemesSidebar {...props}/>,
  },

  // Settings
  {
    title: 'Skills On Course',
    path: URL.SETTINGS_SKILLS,
    url: URL.SETTINGS_SKILLS,
    parent: URL.SETTINGS,
    main: props => <Skills {...props}/>,
  },
  {
    title: 'Website',
    path: URL.SETTINGS_WEBSITE,
    url: URL.SETTINGS_WEBSITE,
    parent: URL.SETTINGS,
    main: props => <Website {...props}/>,
  },
  {
    title: 'Checkout',
    path: URL.SETTINGS_CHECKOUT,
    url: URL.SETTINGS_CHECKOUT,
    parent: URL.SETTINGS,
    main: props => <Checkout {...props}/>,
  },
  {
    title: 'Redirect',
    path: URL.SETTINGS_REDIRECT,
    url: URL.SETTINGS_REDIRECT,
    parent: URL.SETTINGS,
    main: props => <Redirect {...props}/>,
  },

  // History
  {
    title: 'History',
    path: '/history',
    url: URL.HISTORY,
    main: props => <History {...props}/>,
  },

  // Login
  {
    title: 'Login',
    path: '/login',
    url: URL.LOGIN,
    isPublic: true,
    main: () => <Login/>,
  },
];

