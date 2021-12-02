import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {SuggestionRequest} from "../model/v2/suggestion/SuggestionRequest";
import {SuggestionResponse} from "../model/v2/suggestion/SuggestionResponse";

export class SuggestionApi {
  constructor(private http: HttpService) {
  }

  getSuggestion(suggestionRequest: SuggestionRequest): Promise<SuggestionResponse> {
    return this.http.POST(`/v1/suggestion`, suggestionRequest);
  }
}
