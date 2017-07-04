import {schema} from "normalizr";
import {Contact} from "./model/web/Contact";

export const ClassesSchema = new schema.Entity('classes');
export const ClassesListSchema = new schema.Array(ClassesSchema);

export const ProductsSchema = new schema.Entity('products');
export const ProductsListSchema = new schema.Array(ProductsSchema);

export const PromotionsSchema = new schema.Entity('promotions');

export interface ContactsState {
  result: string[];
  entities: {
    contact: { [key: string]: Contact },
  };
}


export const ContactSchema = new schema.Entity('contact');
export const ContactsSchema = new schema.Array(ContactSchema);
