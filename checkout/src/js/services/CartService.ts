import {CartApi} from "../http/CartApi";
import {Token} from "../model";

export class CartService extends CartApi {
  create(id: string, checkout: string): Promise<Token> {
    return super.create(id,checkout);
  }
  _delete(id: string): Promise<any> {
    return super._delete(id);
  }
}
