import {Product} from "../../js/model/web/Product";
import {ProductsParams} from "../../js/model/web/ProductsParams";
import {ProductsApi} from "../../js/http/ProductsApi";
import {MockConfig} from "./mocks/MockConfig";

export class ProductsApiMock extends ProductsApi {
  public config: MockConfig;

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

  getProducts(productsParams: ProductsParams): Promise<Product[]> {
    return this.config.createResponse(this.config.db.products.entities.products);
  }
}
