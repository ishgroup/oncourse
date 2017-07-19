import {CartApi} from "../../js/http/CartApi";
import {Token} from "../../js/model";

export class CartApiMock extends CartApi {
  cartPost(): Promise<Token> {
    return super.cartPost();
  }
}
