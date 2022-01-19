import { DefaultHttpService } from "../../../../common/services/HttpService";
import { Diff, VoucherProduct, VoucherProductApi } from "@api/model";

class VoucherProductService {
  readonly voucherProductApi = new VoucherProductApi(new DefaultHttpService());

  public getVoucherProduct(id: number): Promise<any> {
    return this.voucherProductApi.get(id);
  }

  public updateVoucherProduct(id: number, voucherProduct: VoucherProduct): Promise<any> {
    return this.voucherProductApi.update(id, voucherProduct);
  }

  public createVoucherProduct(voucherProduct: VoucherProduct): Promise<any> {
    return this.voucherProductApi.create(voucherProduct);
  }

  public bulkChange(diff: Diff): Promise<any> {
    return this.voucherProductApi.bulkChange(diff);
  }
}

export default new VoucherProductService();
