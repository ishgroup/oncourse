import {Injector} from "../../injector";
import {SearchApi} from "../../http/SearchApi";

export class SearchService {
  constructor(private searchApi: SearchApi) {
  }

  public getPreparedSuburbs = (text) => {
    return this.searchApi.getSuburbs(text).then(e =>
      Promise.resolve(e.map(item => ({key: item.key, value: item.value.suburb})))
    );
  };

  public getCountries = (text) => {
    return this.searchApi.getCountries(text);
  };
}

const {
  searchApi,
} = Injector.of();

export default new SearchService(searchApi);
