export const DefaultConfig = {
  CSS_PATH: 'http://localhost:8081/assets/cms.css',
  COOKIE_NAME: 'editor',
  CONTAINER_ID: "cms-root",
};

export const API = {

  // Authorization end points
  LOGIN: "/getUser",
  LOGOUT: "/logout",

  // Menu page
  GET_MENU: "/getMenuItems",

  // Pages
  GET_PAGES: "/getPages",
  SAVE_PAGE: "/savePage",
  DELETE_PAGE: "/deletePage",
};
