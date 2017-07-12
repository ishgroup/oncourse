import {HttpService} from "../common/services/HttpService";
import {Product} from "../model/web/Product";
import {ProductsParams} from "../model/web/ProductsParams";

export class ProductsApi {
  constructor(private http: HttpService) {
  }

  getProducts(productsParams: ProductsParams): Promise<Product[]> {
    return this.http.POST(`/products`, productsParams);
  }
}
