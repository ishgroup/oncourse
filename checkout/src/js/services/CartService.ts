import { CartApi } from '../http/CartApi';
import { Article, ContactNode, Enrolment, FieldHeading, Membership, Token, Voucher, WaitingList } from '../model';
import { DefaultHttpService } from '../common/services/HttpService';
import { StoreCartContact, StoreCartItem } from '../model/checkout/request/StoreCart';

class CartService extends CartApi {
  public create(id: string, checkout: string): Promise<Token> {
    return super.create(id, checkout);
  }

  public get(id: string): Promise<any> {
    return super.get(id);
  }

  public _delete(id: string): Promise<any> {
    return super._delete(id);
  }

  public mapCustomFields = (fh: FieldHeading[]) => (fh.length
    && fh.some((f) => f.fields.some((fhf) => fhf.value))
    ? {
      customFields: fh.map((f) => ({
        fields: f.fields
          .filter((fhf) => fhf.value)
          .map(({ id, value }) => ({ id, value }))
      }))
    }
    : {});

  public mapCourse = (course: Enrolment): StoreCartItem => ({
    id: course.classId,
    selected: course.selected,
    ...this.mapCustomFields(course.fieldHeadings)
  });

  public mapWaitingCourse = (course: WaitingList): StoreCartItem => ({
    id: course.courseId,
    selected: course.selected,
    ...this.mapCustomFields(course.fieldHeadings)
  });

  public mapProduct = (product: Article | Membership | Voucher): StoreCartItem => ({
    id: product.productId,
    selected: product.selected,
    ...this.mapCustomFields(product.fieldHeadings)
  });

  public contactNodeToCartContact = (node: ContactNode): StoreCartContact => {
    const cartContact: StoreCartContact = {
      contactId: node.contactId,
      applications: node.applications.map(this.mapCourse),
      classes: node.enrolments.map(this.mapCourse),
      waitingCourses: node.waitingLists.map(this.mapWaitingCourse),
      products: [
        ...node.articles.map(this.mapProduct),
        ...node.vouchers.map(this.mapProduct),
        ...node.memberships.map(this.mapProduct),
      ],
    };

    if (!cartContact.classes.length) {
      delete cartContact.classes;
    }
    if (!cartContact.waitingCourses.length) {
      delete cartContact.waitingCourses;
    }
    if (!cartContact.products.length) {
      delete cartContact.products;
    }

    return cartContact;
  };
}

export default new CartService(new DefaultHttpService());
