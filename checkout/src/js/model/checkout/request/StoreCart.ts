import { ContactNodeRequest } from './ContactNodeRequest';

export type StoreCartRequest  = {
  payerId?: string,
  checkoutURL: string;
  total: string;
} & {
  [key: string]: ContactNodeRequest | string
}

