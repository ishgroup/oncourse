import uuid from "uuid";

import {Item} from "../../js/model/common/Item";
import {SearchApi} from "../../js/http/SearchApi";
import {MockConfig} from "./mocks/MockConfig";

export class SearchApiMock extends SearchApi {
  public config: MockConfig;

  public id: string = uuid();

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

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
