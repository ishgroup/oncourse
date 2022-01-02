import {
 Diff, ProductItem, ProductItemApi, ProductItemCancel 
} from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class SalesService {
  readonly salesApi = new ProductItemApi(new DefaultHttpService());

  public getSale(id: number): Promise<any> {
    return this.salesApi.get(id);
  }

  public updateSale(id: number, productItem: ProductItem): Promise<any> {
    return this.salesApi.update(id, productItem);
  }

  public cancelSale(productItemCancel: ProductItemCancel): Promise<any> {
    return this.salesApi.cancel(productItemCancel);
  }

  public bulkChange(diff: Diff): Promise<any> {
    return this.salesApi.bulkChange(diff);
  }
}

export default new SalesService();
