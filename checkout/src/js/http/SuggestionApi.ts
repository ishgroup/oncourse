import { HttpService } from '../common/services/HttpService';
import { SuggestionResponse } from '../model/v2/suggestion/SuggestionResponse';

export class SuggestionApi {
  constructor(private http: HttpService) {
  }

  getSuggestion(courseIds: string, productIds: string): Promise<SuggestionResponse> {
    return this.http.GET('/v1/suggestion', { params: { courseIds, productIds } });
  }
}
