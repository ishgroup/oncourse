import {CartApi} from "../http/CartApi";
import {Token} from "../model/web/Token";

export class CartApiStub extends CartApi {
  cartPost(): Promise<Token> {
    return super.cartPost();
  }
}
