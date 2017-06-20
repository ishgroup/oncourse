import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {Item} from "../model/common/Item";

export class SearchApi {
  constructor(private http: HttpService) {
  }

  getCountries(text: string): Promise<Item[]> {
    return this.http.GET(`/country/${text}`);
  }
  getLanguages(text: string): Promise<Item[]> {
    return this.http.GET(`/language/${text}`);
  }
  getPostcodes(text: string): Promise<Item[]> {
    return this.http.GET(`/postcode/${text}`);
  }
  getSuburbs(text: string): Promise<Item[]> {
    return this.http.GET(`/suburb/${text}`);
  }
}
