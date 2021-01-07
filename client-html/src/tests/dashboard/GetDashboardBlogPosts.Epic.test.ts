import { DefaultEpic } from "../common/Default.Epic";
import { EpicGetDashboardBlogPosts } from "../../js/containers/dashboard/epics/EpicGetDashboardBlogPosts";
import { GET_BLOG_POSTS_FULFILLED, getDashboardBlogPosts } from "../../js/containers/dashboard/actions";

describe("Get dashboard blog posts epic tests", () => {
  it("GetDashboardBlogPosts should returns correct values", () => DefaultEpic({
    action: getDashboardBlogPosts(),
    epic: EpicGetDashboardBlogPosts,
    processData: mockedApi => {
      const posts = JSON.parse(mockedApi.db.getDashboardFeeds());
      return [
        {
          type: GET_BLOG_POSTS_FULFILLED,
          payload: { blogPosts: posts.entry }
        }
      ];
    }
  }));
});
