import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";

export const GET_DASHBOARD_SEARCH = _toRequestType("get/dashboard/search");
export const GET_DASHBOARD_SEARCH_FULFILLED = FULFILLED(GET_DASHBOARD_SEARCH);

export const GET_DASHBOARD_STATISTIC = _toRequestType("get/dashboard/statistic");
export const GET_DASHBOARD_STATISTIC_FULFILLED = FULFILLED(GET_DASHBOARD_STATISTIC);

export const GET_BLOG_POSTS = _toRequestType("get/dashboard/blog");
export const GET_BLOG_POSTS_FULFILLED = FULFILLED(GET_BLOG_POSTS);

export const getDashboardBlogPosts = () => ({
  type: GET_BLOG_POSTS
});

export const getDashboardStatistic = () => ({
  type: GET_DASHBOARD_STATISTIC
});

export const getDashboardSearch = (search: string) => ({
  type: GET_DASHBOARD_SEARCH,
  payload: { search }
});
