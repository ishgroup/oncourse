import {HttpService} from "../common/services/HttpService";
import {Item} from "../model/autocomplete/Item";

export class AutocompleteApi {
  constructor(private http: HttpService) {
  }

  autocomplete(key: string, text: string): Promise<Item[]> {
    return this.http.GET(`/completion/${key}`, { params: { text }})
  }
}
