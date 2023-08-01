import { Discount, DiscountApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class DiscountService {
  readonly discountApi = new DiscountApi(new DefaultHttpService());

  public getDiscount(id: number): Promise<Discount> {
    return this.discountApi.get(id);
  }

  public updateDiscount(id: number, discount: Discount): Promise<any> {
    return this.discountApi.update(id, discount);
  }

  public createDiscount(discount: Discount): Promise<any> {
    return this.discountApi.create(discount);
  }

  public removeDiscount(id: number): Promise<any> {
    return this.discountApi.remove(id);
  }
}

export default new DiscountService();
