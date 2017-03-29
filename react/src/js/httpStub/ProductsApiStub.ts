import {HttpService} from "../services/HttpService";
import { ModelError } from "../model/ModelError";
import { Product } from "../model/Product";
import { ProductsParams } from "../model/ProductsParams";
import {ProductsApi} from "../http/ProductsApi";

export class ProductsApiStub extends ProductsApi {

  getProducts(productsParams: ProductsParams): Promise<Product[]> {
    return super.getProducts(productsParams);
  }
}
