import { DashboardApi, SearchGroup, StatisticData } from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class DashboardService {
  readonly service = new DefaultHttpService();

  readonly dashboardApi = new DashboardApi(this.service);

  public getStatisticData(): Promise<StatisticData> {
    return this.dashboardApi.getStatistic();
  }

  public getSearchResults(search: string): Promise<SearchGroup[]> {
    return this.dashboardApi.getSearchResults(search);
  }

  public getBlogPosts(): Promise<JSON> {
    return this.service.GET("https://www.ish.com.au/oncourse-news/news.json");
  }

  public logout(): Promise<any> {
    // TODO add default service implementation
    return this.service.PUT("/v1/logout");
  }
}

export default new DashboardService();
