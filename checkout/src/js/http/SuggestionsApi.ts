/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {HttpService} from "../common/services/HttpService";
import {Suggestion, SuggestionsParams} from "../model/web/Suggestion";

export class SuggestionsApi {
  constructor(private http: HttpService) {
  }

  getSuggestions(suggestionsParams: SuggestionsParams): Promise<Suggestion[]> {
    // @todo: Replace this one with suggestion api call.
    return this.http.POST(`/v1/products`, suggestionsParams);
  }
}
