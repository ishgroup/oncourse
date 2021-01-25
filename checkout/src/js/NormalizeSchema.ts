import {schema} from "normalizr";
import {ContactState} from "./services/IshState";

export const ClassesSchema = new schema.Entity('classes');
export const ClassesListSchema = new schema.Array(ClassesSchema);

export const WaitingCoursesSchema = new schema.Entity('waitingCourses');
export const WaitingCoursesListSchema = new schema.Array(WaitingCoursesSchema);

export const InactiveCoursesSchema = new schema.Entity('inactiveCourses');
export const InactiveCoursesListSchema = new schema.Array(InactiveCoursesSchema);

export const ProductsSchema = new schema.Entity('products');
export const ProductsListSchema = new schema.Array(ProductsSchema);

export const PromotionsSchema = new schema.Entity('promotions');

export interface ContactsState {
  result: string[];
  entities: {
    contact: { [key: string]: ContactState },
  };
}


export const ContactSchema = new schema.Entity('contact');
export const ContactsSchema = new schema.Array(ContactSchema);
