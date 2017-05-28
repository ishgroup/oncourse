import {Product} from "../../js/model/web/Product";
import {ProductsParams} from "../../js/model/web/ProductsParams";
import {ProductsApi} from "../../js/http/ProductsApi";

export class ProductsApiMock extends ProductsApi {

  getProducts(productsParams: ProductsParams): Promise<Product[]> {
    return Promise.resolve([{
      "id": "102",
      "code": "GV1",
      "name": "Gift Voucher 100$",
      "description": "{image name:\"Troll\"}",
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "103",
      "code": "GV2",
      "name": "Gift Voucher 50$",
      "description": "{image name:\"Troll\"}",
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "104",
      "code": "GV3",
      "name": "Gift Voucher 500$",
      "description": "{image name:\"Yeoh\"}",
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "105",
      "code": "TV1",
      "name": "Test v3",
      "description": "{image name:\"Yeoh\"}",
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "106",
      "code": "CV1",
      "name": "Class Voucher 1",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "110",
      "code": "asad",
      "name": "asdqwerge",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "111",
      "code": "NONT",
      "name": "Non Tax Voucher 1",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "114",
      "code": "asda",
      "name": "1123123",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "115",
      "code": "CV2",
      "name": "Class Voucher 2",
      "description": "TAASDAS QWeqeq w w wgehawe agw wageymsdnfgxcvzsd rseherx sdfsgawe ",
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "116",
      "code": "INV1",
      "name": "Voucher of Invisibilty 1",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "122",
      "code": "NV1",
      "name": "New Voucher",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "128",
      "code": "CV3",
      "name": "Changed Voucher",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "142",
      "code": "100CS",
      "name": "100$ Course Voucher",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "150",
      "code": "SV",
      "name": "MEGA SUPER ULTRA VOUCHER!",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "152",
      "code": "v2500",
      "name": "$2500 Voucher",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "153",
      "code": "UNI",
      "name": "Unique Voucher",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "154",
      "code": "CV4",
      "name": "Class Voucher 4",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "169",
      "code": "MV1",
      "name": "Mass Voucher",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "170",
      "code": "100mem",
      "name": "100 Day Membership",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }, {
      "id": "176",
      "code": "1dm",
      "name": "1 Day Membership",
      "description": null,
      "isPaymentGatewayEnabled": true,
      "canBuy": true
    }]);
  }
}
