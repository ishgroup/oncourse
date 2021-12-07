import {
  CourseClassPrice, Course, Preferences, Promotion, CourseClass, Product, Contact
} from '../model';
import { CheckoutState } from '../enrol/reducers/State';
import { WillowConfig } from '../configLoader';
import { SuggestionResponse } from '../model/v2/suggestion/SuggestionResponse';

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
  readonly suggestions: SuggestionsState;
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
export type SuggestionsState = SuggestionResponse;

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

export type CourseClassPriceState = CourseClassPrice;

export type CourseClassCart = CourseClass;

export interface ContactState extends Contact {
  parent?: Contact;
  warning?: string;
}

export type ProductCart = Product;

export type WaitingCourseCart = Course;

export type PromotionCart = Promotion;

export interface Normalized<V> {
  readonly entities: { [key: string]: V };
  readonly result: any;
}
