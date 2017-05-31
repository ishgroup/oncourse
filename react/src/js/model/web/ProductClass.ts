import { CourseClassPrice } from "./../web/CourseClassPrice";
import {Product} from "./Product";

export class ProductClass {

    /**
     * Internal Unique identifier of class
     */
    id?: string;
    product?: Product;

    /**
     * Code of class
     */
    code?: string;

    /**
     * Is class cancelled
     */
    isCancelled?: boolean;

    /**
     * Is payment gateway enabled
     */
    isPaymentGatewayEnabled?: boolean;

    /**
     * Prices attached to current course class
     */
    price?: string;

    /**
     * Is on sale and is Web visible
     */
    canBuy?: boolean;
}

