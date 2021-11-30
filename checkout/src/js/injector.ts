import assignIn from "lodash/assignIn";
import {HttpService, DefaultHttpService} from "./common/services/HttpService";
import {CartApi} from "./http/CartApi";
import {ContactApi} from "./http/ContactApi";
import {CourseClassesApi} from "./http/CourseClassesApi";
import {ProductsApi} from "./http/ProductsApi";
import {PromotionApi} from "./http/PromotionApi";
import {CorporatePassApi} from "./http/CorporatePassApi";
import {MergeService} from "./services/MergeService";
import {LegacySyncStorage} from "./services/LegacySyncStorage";
import {CheckoutApi} from "./http/CheckoutApi";
import {SearchApi} from "./http/SearchApi";
import {PreferenceApi} from "./http/PreferenceApi";
import {SuggestionsApi} from "./http/SuggestionsApi";

export class Injector {
  readonly http: HttpService = new DefaultHttpService();
  readonly cartApi = new CartApi(this.http);
  readonly contactApi = new ContactApi(this.http);
  readonly courseClassesApi = new CourseClassesApi(this.http);
  readonly productsApi = new ProductsApi(this.http);
  readonly promotionApi = new PromotionApi(this.http);
  readonly checkoutApi = new CheckoutApi(this.http);
  readonly searchApi = new SearchApi(this.http);
  readonly preferenceApi = new PreferenceApi(this.http);
  readonly corporatePassApi = new CorporatePassApi(this.http);
  readonly mergeService = new MergeService();
  readonly legacySyncStorage = new LegacySyncStorage();
  readonly suggestionsApi = new SuggestionsApi(this.http);

  setService<T extends Injector[K], K extends keyof Injector>(name: K, service: T) {
    assignIn(this[name], service);
  }

  private static injectors: {[key: string]: Injector} = {};

  static of(name = "default"): Injector {
    if (this.injectors[name]) {
      return this.injectors[name];
    }

    const injector = new Injector();
    this.injectors[name]  = injector;
    return injector;
  }
}
