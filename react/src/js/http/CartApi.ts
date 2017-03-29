import {HttpService} from "../services/HttpService";
import { ModelError } from "../model/ModelError";
import { Token } from "../model/Token";

export class CartApi {
  constructor(private http: HttpService) {
  }

  cartPost(): Promise<Token> {
    return this.http.POST(`/cart`)
  }
}
