import {HttpService} from "../services/HttpService";
import { ModelError } from "../model/ModelError";

export class AutocompleteApi {
  constructor(private http: HttpService) {
  }

  autocomplete(key: string, text: string): Promise<string[]> {
    return this.http.GET(`/completion/${key}`, { params: { text }})
  }
}
