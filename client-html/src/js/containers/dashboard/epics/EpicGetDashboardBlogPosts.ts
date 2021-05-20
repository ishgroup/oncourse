/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_BLOG_POSTS, GET_BLOG_POSTS_FULFILLED } from "../actions";
import DashboardService from "../services/DashboardService";

const request: EpicUtils.Request = {
  type: GET_BLOG_POSTS,
  getData: () => DashboardService.getBlogPosts(),
  processData: blogPosts => {
    const posts = blogPosts ? JSON.parse(blogPosts) : { entry: [] };
    return [
      {
        type: GET_BLOG_POSTS_FULFILLED,
        payload: { blogPosts: posts.entry }
      }
    ];
  },
  processError: error => {
    console.error(error);

    return [];
  }
};

export const EpicGetDashboardBlogPosts: Epic<any, any> = EpicUtils.Create(request);
