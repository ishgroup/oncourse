export const DefaultConfig = {
  CSS_PATH: 'http://localhost:8081/assets/cms.css',
  COOKIE_NAME: 'editor',
  CONTAINER_ID: "cms-root",
};

export const API = {

  // Authorization end points
  LOGIN: "/user.login",
  GET_USER: "/user.get",
  LOGOUT: "/user.logout",

  // Menu page
  GET_MENU: "/menu.list",
  SAVE_MENU: "/menu.update",

  // Pages
  GET_PAGES: "/page.list",
  GET_PAGE_BY_URL: "/page.get",
  SAVE_PAGE: "/page.update",
  ADD_PAGE: "/page.create",
  DELETE_PAGE: /page\.delete\/\d+/,

  // Blocks
  GET_BLOCKS: "/block.list",
  SAVE_BLOCK: "/block.update",
  ADD_BLOCK: "/block.create",
  DELETE_BLOCK: /block\.delete\/\d+/,

  // Themes
  GET_THEMES: "/theme.list",
  GET_LAYOUTS: "/layout.list",
  SAVE_THEME: "/theme.update",
  ADD_THEME: "/theme.create",
  DELETE_THEME: /theme\.delete\/\d+/,

  // History
  GET_VERSIONS: "/version.list",
  SET_VERSION: /version\.draft\.set\/\d+/,
  PUBLISH: "/version.draft.publish",

  // Settings
  GET_SKILLS_ON_COURSE_SETTINGS: "/settings.skillsOnCourse.get",
  SET_SKILLS_ON_COURSE_SETTINGS: "/settings.skillsOnCourse.set",
  GET_REDIRECT_SETTINGS: "/redirect.list",
  SET_REDIRECT_SETTINGS: "/redirect.update",
  GET_WEBSITE_SETTINGS: "/settings.website.get",
  SET_WEBSITE_SETTINGS: "/settings.website.set",
};
