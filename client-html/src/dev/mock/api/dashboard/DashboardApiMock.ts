import { promiseResolve } from "../../MockAdapter";

export function dashboardApiMock(mock) {
  /**
   * List items
   * */
  this.api.onGet("/v1/dashboard/statistic").reply(config => promiseResolve(config, this.db.getStatistic()));

  this.api.onGet("/v1/user/preference/category").reply(config => promiseResolve(config, this.db.getCategories()));

  this.api.onPut("/v1/user/preference/category").reply(config => promiseResolve(config, {}));

  this.api.onGet("https://oncourse-news.s3.ap-southeast-2.amazonaws.com/news.json").reply(config => promiseResolve(config, JSON.parse(this.db.getDashboardFeeds())));

  this.api.onGet("/v1/dashboard/search").reply(config => promiseResolve(config, this.db.getDashboardSearchResult()));
}
