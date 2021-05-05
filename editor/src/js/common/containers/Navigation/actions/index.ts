export const SHOW_NAVIGATION: string = "SHOW_NAVIGATION";
export const HIDE_NAVIGATION: string = "HIDE_NAVIGATION";

export const showNavigation = () => ({
  type: SHOW_NAVIGATION,
});

export const hideNavigation = () => ({
  type: HIDE_NAVIGATION,
});
