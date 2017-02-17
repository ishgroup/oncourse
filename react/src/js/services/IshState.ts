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

export type CommonCartItem = Course | Product | Discount;

export interface Course {
    readonly id: number;
    readonly name: string;
    readonly uniqueIdentifier: string;
}

export interface Product {
  readonly id: number;
  readonly name: string;
  readonly uniqueIdentifier: string;

  pending: boolean;
}

export interface Discount {
  readonly id: number; // todo: check
}
