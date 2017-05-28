import {SearchApiMock} from "./SearchApiMock";
import {CheckoutApiMock} from "./CheckoutApiMock";
import {PromotionApiMock} from "./PromotionApiMock";
import {ProductsApiMock} from "./ProductsApiMock";
import {CourseClassesApiMock} from "./CourseClassesApiMock";
import {ContactApiMock} from "./ContactApiMock";
import {CartApiMock} from "./CartApiMock";
import {EnvironmentConstants} from "../../js/config/EnvironmentConstants";
import {Injector} from "../../js/injector";


export const init = (injector: Injector) => {
  if (process.env.NODE_ENV === EnvironmentConstants.development) {
    // Overwrite Services for development without real server
    injector.setService("cartApi", new CartApiMock(injector.http));
    injector.setService("contactApi", new ContactApiMock(injector.http));
    injector.setService("courseClassesApi", new CourseClassesApiMock(injector.http));
    injector.setService("productsApi", new ProductsApiMock(injector.http));
    injector.setService("promotionApi", new PromotionApiMock(injector.http));
    injector.setService("checkoutApi", new CheckoutApiMock(injector.http));
    injector.setService("searchApi", new SearchApiMock(injector.http));
  }
};
