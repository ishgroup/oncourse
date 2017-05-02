import {HttpService} from "../common/services/HttpService";
import { CommonError } from "../model/common/CommonError";
import { Token } from "../model/web/Token";

export class CartApi {
  constructor(private http: HttpService) {
  }

  cartPost(): Promise<Token> {
    return this.http.POST(`/cart`)
  }
}
