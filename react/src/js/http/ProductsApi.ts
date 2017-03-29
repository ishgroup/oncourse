import {HttpService} from "../services/HttpService";
import { ModelError } from "../model/ModelError";
import { Product } from "../model/Product";
import { ProductsParams } from "../model/ProductsParams";

export class ProductsApi {
  constructor(private http: HttpService) {
  }

  getProducts(productsParams: ProductsParams): Promise<Product[]> {
    return this.http.POST(`/products`, productsParams)
  }
}
