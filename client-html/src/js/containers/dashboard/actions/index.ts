import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";
import { Category, PreferenceEnum, UserPreference } from "@api/model";

export const GET_DASHBOARD_SEARCH = _toRequestType("get/dashboard/search");
export const GET_DASHBOARD_SEARCH_FULFILLED = FULFILLED(GET_DASHBOARD_SEARCH);

export const GET_DASHBOARD_STATISTIC = _toRequestType("get/dashboard/statistic");
export const GET_DASHBOARD_STATISTIC_FULFILLED = FULFILLED(GET_DASHBOARD_STATISTIC);

export const GET_DASHBOARD_CATEGORIES = _toRequestType("get/user/preference/category");
export const GET_DASHBOARD_CATEGORIES_FULFILLED = FULFILLED(GET_DASHBOARD_CATEGORIES);

export const GET_BLOG_POSTS = _toRequestType("get/dashboard/blog");
export const GET_BLOG_POSTS_FULFILLED = FULFILLED(GET_BLOG_POSTS);

export const SET_DASHBOARD_FAVORITES = _toRequestType("put/user/preference/category");
export const SET_DASHBOARD_FAVORITES_FULFILLED = FULFILLED(SET_DASHBOARD_FAVORITES);

export const GET_FAVORITE_SCRIPTS = _toRequestType("get/user/preference/favorite/scripts");
export const GET_FAVORITE_SCRIPTS_FULFILLED = FULFILLED(GET_FAVORITE_SCRIPTS);

export const SET_FAVORITE_SCRIPTS = _toRequestType("put/user/preference/favorite/scripts");
export const SET_FAVORITE_SCRIPTS_FULFILLED = FULFILLED(SET_FAVORITE_SCRIPTS);

export const getDashboardBlogPosts = () => ({
  type: GET_BLOG_POSTS
});

export const getDashboardCategories = () => ({
  type: GET_DASHBOARD_CATEGORIES
});

export const setDashboardFavorites = (categories: Category[]) => ({
  type: SET_DASHBOARD_FAVORITES,
  payload: categories
});

export const getFavoriteScripts = () => ({
  type: GET_FAVORITE_SCRIPTS
});

export const setFavoriteScripts = (scripts: number[]) => ({
  type: SET_FAVORITE_SCRIPTS,
  payload: scripts
});

export const getDashboardStatistic = () => ({
  type: GET_DASHBOARD_STATISTIC
});

export const getDashboardSearch = (search: string) => ({
  type: GET_DASHBOARD_SEARCH,
  payload: { search }
});
