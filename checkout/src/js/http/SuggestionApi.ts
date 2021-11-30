import { HttpService } from '../common/services/HttpService';
import { SuggestionResponse } from '../model/v2/suggestion/SuggestionResponse';

export class SuggestionApi {
  constructor(private http: HttpService) {
  }

  getSuggestion(courseIds: string, productIds: string): Promise<SuggestionResponse> {
    const query = new URLSearchParams();
    if (courseIds?.length) {
      query.append('courseIds', courseIds);
    }
    if (productIds?.length) {
      query.append('productIds', productIds);
    }
    return this.http.GET(`/v1/suggestion?${query}`);
  }
}
