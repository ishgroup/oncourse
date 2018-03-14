export const DefaultConfig = {
  CSS_PATH: 'http://localhost:8081/assets/cms.css',
  COOKIE_NAME: 'editor',
  CONTAINER_ID: "cms-root",
};

const v = 'v1';

export const API = {

  // Authorization end points
  USER: `/${v}/user`,
  SESSION: `/${v}/session`,

  // Menu page
  MENU: `/${v}/menu`,

  // Pages
  PAGE: `/${v}/page`,
  PAGE_DELETE: new RegExp(`${v}/page/\\d+`),

  // Blocks
  BLOCK: `/${v}/block`,
  BLOCK_DELETE: new RegExp(`${v}/block/\\d+`),

  // Themes
  THEME: `/${v}/theme`,
  LAYOUT: `/${v}/layout`,
  THEME_DELETE: new RegExp(`${v}/theme/\\d+`),

  // Versions
  VERSION: `/${v}/version`,
  VERSION_UPDATE: new RegExp(`${v}/version/\\d+`),

  // Settings
  SKILLS_ON_COURSE: `/${v}/settings/skillsOnCourse`,
  WEBSITE: `/${v}/settings/website`,
  CHECKOUT: `/${v}/settings/checkout`,

  // Redirects
  REDIRECT: `/${v}/redirect`,
};
