export const DefaultConfig = {
  CSS_PATH: 'http://localhost:8081/assets/cms.css',
  COOKIE_NAME: 'editor',
  CONTAINER_ID: "cms-root",
};

export const API = {

  // Authorization end points
  USER: "/user",
  SESSION: "/session",

  // Menu page
  MENU: "/menu",

  // Pages
  PAGE: "/page",
  PAGE_DELETE: /page\/\d+/,

  // Blocks
  BLOCK: "/block",
  BLOCK_DELETE: /block\/\d+/,

  // Themes
  THEME: "/theme",
  LAYOUT: "/layout",
  THEME_DELETE: /theme\/\d+/,

  // Versions
  VERSION: "/version",
  VERSION_UPDATE: /version\/\d+/,

  // Settings
  SKILLS_ON_COURSE: "/settings/skillsOnCourse",
  WEBSITE: "/settings/website",

  // Redirects
  REDIRECT: "/redirect",
};
