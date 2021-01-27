import {CourseClassPrice, Course, Preferences, Promotion, CourseClass, Product, Contact, Discount} from "../model";
import {CheckoutState} from "../enrol/reducers/State";
import {WillowConfig} from "../configLoader";

export interface IshState {
  readonly form: any;
  readonly cart: CartState;
  readonly popup: PopupState;
  readonly courses: CoursesState;
  readonly waitingCourses: WaitingCoursesState;
  readonly inactiveCourses: InactiveCoursesState;
  readonly products: ProductsState;
  readonly checkout: CheckoutState;
  readonly config: WillowConfig;
  readonly preferences: Preferences;

}

export interface CartState {
  readonly contact: ContactState;
  readonly courses: CourseClassCartState;
  readonly products: ProductCartState;
  readonly promotions: PromotionCartState;
  readonly waitingCourses: WaitingCourseClassState;
}

export interface PopupState {
  readonly content: string;
}

export type CoursesState = Normalized<CourseClass>;
export type WaitingCoursesState = Normalized<Course>;
export type InactiveCoursesState = Normalized<Course>;
export type ProductsState = Normalized<Product>;

/**
 * @deprecated we will use separate classes
 */
export type CommonCartItem = CourseClassCart | ProductCart | PromotionCart | WaitingCourseCart;

export type CourseClassCartState = Normalized<CourseClassCart>;
export type ProductCartState = Normalized<ProductCart>;
export type PromotionCartState = Normalized<PromotionCart>;
export type WaitingCourseClassState = Normalized<WaitingCourseCart>;
export type ReplaceCourseClassState = { replace: CourseClassCartState, replacement: CourseClassCartState };

// --- Extend backend model
export interface DiscountState extends Discount {
}

export interface CourseClassPriceState extends CourseClassPrice {
}

export interface CourseClassCart extends CourseClass {
}


export interface ContactState extends Contact {
  parent?: Contact;
  warning?: string;
}

export interface ProductCart extends Product {
}

export interface WaitingCourseCart extends Course {
}

export interface PromotionCart extends Promotion {
}

export interface Normalized<V> {
  readonly entities: { [key: string]: V };
  readonly result: any;
}
