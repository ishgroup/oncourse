import { ContactNodeRequest } from './ContactNodeRequest';

export type StoreCartRequest = {
  payerId?: string,
  checkoutURL: string;
} & {
  [key: string]: ContactNodeRequest | string
};
