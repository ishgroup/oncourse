export const DefaultConfig = {
  CSS_PATH: 'http://localhost:8081/assets/cms.css',
  COOKIE_NAME: 'editor',
  CONTAINER_ID: "cms-root",
};

export const API = {

  // Authorization end points
  LOGIN: "/login",
  GET_USER: "/getUser",
  LOGOUT: "/logout",

  // Menu page
  GET_MENU: "/getMenuItems",
  SAVE_MENU: "/saveMenuItems",

  // Pages
  GET_PAGES: "/getPages",
  GET_PAGE_BY_URL: "/getPageByUrl",
  SAVE_PAGE: "/savePage",
  ADD_PAGE: "/addPage",
  DELETE_PAGE: /deletePage\/\d+/,

  // Blocks
  GET_BLOCKS: "/getBlocks",
  SAVE_BLOCK: "/saveBlock",
  ADD_BLOCK: "/addBlock",
  DELETE_BLOCK: "/deleteBlock",

  // Themes
  GET_THEMES: "/getThemes",
  GET_LAYOUTS: "/getLayouts",
  SAVE_THEME: "/saveTheme",
  ADD_THEME: "/addTheme",
  DELETE_THEME: "/deleteTheme",

  // History
  GET_VERSIONS: "/getVersions",
  SET_VERSION: "/setVersion",
  PUBLISH: "/publish",

  // Settings
  GET_CHECKOUT_SETTINGS: "/getCheckoutSettings",
  SET_CHECKOUT_SETTINGS: "/setCheckoutSettings",
  GET_SKILLS_ON_COURSE_SETTINGS: "/getSkillsOnCourseSettings",
  SET_SKILLS_ON_COURSE_SETTINGS: "/setSkillsOnCourseSettings",
  GET_REDIRECT_SETTINGS: "/getRedirectSettings",
  SET_REDIRECT_SETTINGS: "/setRedirectSettings",
  GET_WEBSITE_SETTINGS: "/getWebsiteSettings",
  SET_WEBSITE_SETTINGS: "/setWebsiteSettings",
};
