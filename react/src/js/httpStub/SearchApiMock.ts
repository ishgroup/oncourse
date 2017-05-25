import uuid from "uuid";

import {Item} from "../model/common/Item";
import {SearchApi} from "../http/SearchApi";
import {MockConfig} from "./mocks/MockConfig";

export class SearchApiMock extends SearchApi {
  public config: MockConfig = MockConfig.CONFIG;

  public id: string = uuid();

  getCountries(text: string): Promise<Item[]> {
    return this.config.createResponse(this.config.db.searchCountryBy(text));
  }

  getLanguages(text: string): Promise<Item[]> {
    return this.config.createResponse([]);
  }

  getPostcodes(text: string): Promise<Item[]> {
    return this.config.createResponse([]);
  }

  getSuburbs(text: string): Promise<Item[]> {
    return this.config.createResponse(this.config.db.suburbs);
  }
}
