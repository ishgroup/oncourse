import { Filter, FilterApi } from "@api/model";
import { DefaultHttpService } from "./HttpService";

class CustomFiltersService {
  readonly filterApi = new FilterApi(new DefaultHttpService());

  public deleteCustomFilter(entity: string, id: number): Promise<any> {
    return this.filterApi._delete(entity, id);
  }

  public getFilters(entity: string): Promise<Filter[]> {
    return this.filterApi.get(entity);
  }

  public saveCustomFilter(filter: Filter, rootEntity: string): Promise<any> {
    return this.filterApi.save(rootEntity, filter);
  }
}

export default new CustomFiltersService();
