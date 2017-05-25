import {schema} from "normalizr";

export const ClassesSchema = new schema.Entity('classes');
export const ClassesListSchema = new schema.Array(ClassesSchema);

export const ProductsSchema = new schema.Entity('products');
export const ProductsListSchema = new schema.Array(ProductsSchema);

export const PromotionsSchema = new schema.Entity('promotions');

export const ContactsSchema = new schema.Entity('contacts');
export const ContactsListSchema = new schema.Array(ContactsSchema);
