import { Site, SiteApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class SiteService {
  readonly siteApi = new SiteApi(new DefaultHttpService());

  public createSite(site: Site): Promise<any> {
    return this.siteApi.create(site);
  }

  public getSite(id: number): Promise<any> {
    return this.siteApi.get(id);
  }

  public updateSite(id: number, site: Site): Promise<any> {
    return this.siteApi.update(id, site);
  }

  public removeSite(id: number): Promise<any> {
    return this.siteApi.remove(id);
  }
}

export default new SiteService();
