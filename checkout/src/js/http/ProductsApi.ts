import {HttpService} from "../common/services/HttpService";
import {Product, ProductsParams} from "../model";

export class ProductsApi {
  constructor(private http: HttpService) {
  }

  getProducts(productsParams: ProductsParams): Promise<Product[]> {
    return this.http.POST(`/products`, productsParams);
  }
}
