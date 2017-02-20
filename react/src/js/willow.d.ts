// Generated using typescript-generator version 1.17.284 on 2017-02-17 17:09:53.

declare namespace WillowDefinitions {

    interface Contact {
        id: number;
        firstName: string;
        lastName: string;
        email: string;
    }

    interface Course {
        id: number;
        name: string;
        code: string;
        description: string;
    }

    interface CourseClass {
        id: number;
        course: Course;
        code: string;
    }

    interface CourseClassPrice {
        courseClass: CourseClass;
        discounts: Discount[];
    }

    interface Discount {
        id: number;
        expiryDate: string;
        discountedFee: number;
    }

    interface Product {
        id: number;
        name: string;
        sku: string;
    }

    interface Promotion {
        id: number;
        name: string;
        code: string;
    }

    interface ShoppingCart {
        id: string;
        contact: Contact;
        classes: CourseClass[];
        products: Product[];
        vouchers: Voucher[];
        promotions: Promotion[];
    }

    interface Voucher {
        id: number;
        sku: string;
    }

}
