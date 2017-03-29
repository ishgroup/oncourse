import {CourseClassPrice} from "../model/CourseClassPrice";
import {Promotion} from "../model/Promotion";
import {CourseClass} from "../model/CourseClass";
import {Product} from "../model/Product";
import {Contact} from "../model/Contact";

export interface IshState {
  readonly cart: CartState;
  readonly popup: PopupState;
  readonly courses: CoursesState;
  readonly products: ProductsState;
}

export interface CartState {
  readonly contact: ContactState;
  readonly courses: CourseClassCartState;
  readonly products: ProductCartState;
  readonly promotions: PromotionCartState;
  readonly discounts: PromotionCartState; // replace with promotions
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

interface Normalized<V> {
  readonly entities: { [key: string]: V };
  readonly result: any;
}
