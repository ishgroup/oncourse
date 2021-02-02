import {HttpService} from "../common/services/HttpService";

export class CartApi {
  constructor(private http: HttpService) {
  }

  _delete(id: string): Promise<any> {
    return this.http.DELETE(`/v1/cart/${id}`);
  }
  create(id: string, checkout: string): Promise<any> {
    return this.http.POST(`/v1/cart/${id}`, checkout);
  }
  get(id: string): Promise<string> {
    return this.http.GET(`/v1/cart/${id}`);
  }
}
