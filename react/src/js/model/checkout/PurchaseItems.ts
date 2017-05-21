import { Application } from "./../checkout/Application";
import { Enrolment } from "./../checkout/Enrolment";
import { ProductItem } from "./../checkout/ProductItem";
import { Contact } from "./../web/Contact";

export class PurchaseItems {
  contact?: Contact;
  enrolments?: Enrolment[];
  applications?: Application[];
  productItems?: ProductItem[];
}

