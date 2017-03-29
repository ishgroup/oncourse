import {HttpService} from "../services/HttpService";
import { ModelError } from "../model/ModelError";
import { Product } from "../model/Product";
import { ProductsParams } from "../model/ProductsParams";
import {ProductsApi} from "../http/ProductsApi";

export class ProductsApiStub extends ProductsApi {

  getProducts(productsParams: ProductsParams): Promise<Product[]> {
    return Promise.resolve([{
      "id": "83",
      "code": "TM1",
      "name": "Test Membership",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "85",
      "code": "TP1",
      "name": "Test Product 1",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "146",
      "code": "VOUCH1",
      "name": "Voucher for class",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "147",
      "code": "VOUCH2",
      "name": "Voucher for moneyz",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "172",
      "code": "jship",
      "name": "Julyship",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }]);
  }
}
