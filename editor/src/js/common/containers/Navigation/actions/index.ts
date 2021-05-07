export const SHOW_NAVIGATION: string = "SHOW_NAVIGATION";
export const HIDE_NAVIGATION: string = "HIDE_NAVIGATION";
export const SET_ACTIVE_URL: string = "SET_ACTIVE_URL";

export const showNavigation = () => ({
  type: SHOW_NAVIGATION,
});

export const hideNavigation = () => ({
  type: HIDE_NAVIGATION,
});

export const setActiveUrl = (newUrl: string) => ({
  type: SET_ACTIVE_URL,
  payload: newUrl,
})
