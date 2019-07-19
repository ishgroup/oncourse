import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {Item} from "../model/common/Item";

export class SearchApi {
  constructor(private http: HttpService) {
  }

  getCountries(text: string): Promise<Item[]> {
    return this.http.GET(`/v1/country/${text}`);
  }
  getLanguages(text: string): Promise<Item[]> {
    return this.http.GET(`/v1/language/${text}`);
  }
  getPostcodes(text: string): Promise<Item[]> {
    return this.http.GET(`/v1/postcode/${text}`);
  }
  getSuburbs(text: string): Promise<Item[]> {
    return this.http.GET(`/v1/suburb/${text}`);
  }
}
