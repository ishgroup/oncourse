export interface IshState {
    readonly cart: CartState;
    readonly popup: PopupState;
}


export interface PopupState {
    readonly content: string;
}

export interface CartState {
    readonly courses: Course[];
    readonly products: Product[];
    readonly discounts: Discount[];
}

export interface Course {
    readonly id: number;
    readonly name: string;
}

export interface Product {
}

export interface Discount {
}
