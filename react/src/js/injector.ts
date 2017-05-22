import assignIn from "lodash/assignIn";
import {HttpService, DefaultHttpService} from "./common/services/HttpService";
import {CartApi} from "./http/CartApi";
import {ContactApi} from "./http/ContactApi";
import {CourseClassesApi} from "./http/CourseClassesApi";
import {ProductsApi} from "./http/ProductsApi";
import {PromotionApi} from "./http/PromotionApi";
import {MergeService} from "./services/MergeService";
import {LegacySyncStorage} from "./services/LegacySyncStorage";
import {EnvironmentConstants} from "./config/EnvironmentConstants";
import {CartApiStub} from "./httpStub/CartApiStub";
import {ContactApiStub} from "./httpStub/ContactApiStub";
import {ProductsApiStub} from "./httpStub/ProductsApiStub";
import {CourseClassesApiStub} from "./httpStub/CourseClassesApiStub";
import {PromotionApiStub} from "./httpStub/PromotionApiStub";
import {AutocompleteApi} from "./http/AutocompleteApi";
import {AutocompleteApiStub} from "./httpStub/AutocompleteApiStub";
import {CheckoutApi} from "./http/CheckoutApi";
import {SearchApi} from "./http/SearchApi";

export class Injector {
  readonly http: HttpService = new DefaultHttpService();
  readonly cartApi = new CartApi(this.http);
  readonly contactApi = new ContactApi(this.http);
  readonly courseClassesApi = new CourseClassesApi(this.http);
  readonly productsApi = new ProductsApi(this.http);
  readonly promotionApi = new PromotionApi(this.http);
  readonly autocompleteApi = new AutocompleteApi(this.http);
  readonly checkoutApi = new CheckoutApi(this.http);
  readonly searchApi = new SearchApi(this.http);
  readonly mergeService = new MergeService();
  readonly legacySyncStorage = new LegacySyncStorage();

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

if (process.env.NODE_ENV === EnvironmentConstants.development) {
  const injector = Injector.of();
  // Overwrite Services for development without real server
  injector.setService("cartApi", new CartApiStub(injector.http));
  injector.setService("contactApi", new ContactApiStub(injector.http));
  injector.setService("courseClassesApi", new CourseClassesApiStub(injector.http));
  injector.setService("productsApi", new ProductsApiStub(injector.http));
  injector.setService("promotionApi", new PromotionApiStub(injector.http));
  // injector.setService("checkoutApi", new CheckoutApiStub(injector.http));
  // injector.setService("searchApi", new SearchApiStub(injector.http));
}
