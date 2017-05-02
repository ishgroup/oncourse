import {CourseClassPrice} from "../model/web/CourseClassPrice";
import {Promotion} from "../model/web/Promotion";
import {CourseClass} from "../model/web/CourseClass";
import {Product} from "../model/web/Product";
import {Contact} from "../model/web/Contact";
import {ValidationError} from "../model/common/ValidationError";
import {Suggestion} from "../selectors/autocomplete";

export interface IshState {
  readonly cart: CartState;
  readonly popup: PopupState;
  readonly courses: CoursesState;
  readonly products: ProductsState;
  readonly enrol: EnrolState;
  readonly autocomplete: AutocompleteState;
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

//--- Enrol State
export interface EnrolState {
  payer: PayerState;
}

export interface PayerEntity extends Contact {

}

export interface PayerState {
  entity: PayerEntity;
  error: ValidationError;
}

//-- Autocomplete

export interface AutocompleteState {
  [key: string]: Suggestion[];
}

export interface Normalized<V> {
  readonly entities: { [key: string]: V };
  readonly result: any;
}
