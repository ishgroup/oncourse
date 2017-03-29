import {schema} from "normalizr";

export const classesSchema = new schema.Entity('classes');
export const classesListSchema = new schema.Array(classesSchema);

export const productsSchema = new schema.Entity('products');
export const productsListSchema = new schema.Array(productsSchema);

export const promotionsSchema = new schema.Entity('promotions');


