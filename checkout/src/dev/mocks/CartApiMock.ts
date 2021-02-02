import {CartApi} from "../../js/http/CartApi";
import {Token} from "../../js/model";

export class CartApiMock extends CartApi {
  create(id: string, checkout: string): Promise<Token> {
    return super.create(id,checkout);
  }
}
