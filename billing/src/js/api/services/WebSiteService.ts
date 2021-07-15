import { SiteDTO, WebSiteApi } from '@api/model';
import { DefaultHttpService } from './HttpService';

class WebSiteService {
  readonly webSiteApi = new WebSiteApi(new DefaultHttpService());

  public crateSite(site: SiteDTO): Promise<any> {
    return this.webSiteApi.crateSite(site);
  }

  public deleteSite(id: number): Promise<any> {
    return this.webSiteApi.deleteSite(id);
  }

  public getSites(): Promise<SiteDTO[]> {
    return this.webSiteApi.getSites();
  }

  public updateSite(site: SiteDTO): Promise<any> {
    return this.webSiteApi.updateSite(site);
  }
}

export default new WebSiteService();
