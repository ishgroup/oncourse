import {CourseClassPrice} from "../model/web/CourseClassPrice";
import {Promotion} from "../model/web/Promotion";
import {CourseClass} from "../model/web/CourseClass";
import {Product} from "../model/web/Product";
import {Contact} from "../model/web/Contact";
import {CheckoutState} from "../enrol/reducers/State";
import {Discount} from "../model/web/Discount";

export interface IshState {
  readonly form: any;
  readonly cart: CartState;
  readonly popup: PopupState;
  readonly courses: CoursesState;
  readonly products: ProductsState;
  readonly checkout: CheckoutState;
  readonly checkoutPath: string;
}

export interface CartState {
  readonly contact: ContactState;
  readonly courses: CourseClassCartState;
  readonly products: ProductCartState;
  readonly promotions: PromotionCartState;
}

export interface PopupState {
  readonly content: string;
}

export type CoursesState = Normalized<CourseClass>;
export type ProductsState = Normalized<Product>;

/**
 * @deprecated we will use separate classes
 */
export type CommonCartItem = CourseClassCart | ProductCart | PromotionCart;

export type CourseClassCartState = Normalized<CourseClassCart>;
export type ProductCartState = Normalized<ProductCart>;
export type PromotionCartState = Normalized<PromotionCart>;

//--- Extend backend model
export interface DiscountState extends Discount {
}

export interface CourseClassPriceState extends CourseClassPrice {
}

export interface CourseClassCart extends CourseClass {
}

export interface ContactState extends Contact {
}

export interface ProductCart extends Product {
}

export interface PromotionCart extends Promotion {
}

export interface Normalized<V> {
  readonly entities: { [key: string]: V };
  readonly result: any;
}
