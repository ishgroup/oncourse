import { ActionsObservable, Epic } from "redux-observable";
import { flatMap, mergeMap, catchError } from "rxjs/operators";
import { CheckoutSaleRelation, MembershipProduct } from "@api/model";
import uniqid from "uniqid";
import {
  CHECKOUT_ADD_CONTACT,
  CHECKOUT_ADD_ITEM, CHECKOUT_REMOVE_CONTACT, CHECKOUT_REMOVE_ITEM,
  CHECKOUT_UPDATE_RELATED_ITEMS
} from "../../actions";
import CheckoutService from "../../services/CheckoutService";
import { State } from "../../../../reducers/state";
import { processError } from "../../../../common/epics/EpicUtils";
import { checkoutUpdateSummaryPrices } from "../../actions/checkoutSummary";
import EntityService from "../../../../common/services/EntityService";
import {
  checkoutCourseClassMap, checkoutCourseMap, checkoutProductMap, checkoutVoucherMap, processCheckoutSale
} from "../../utils";
import { getCustomColumnsMap } from "../../../../common/utils/common";
import {
  CHECKOUT_COURSE_CLASS_COLUMNS,
  CHECKOUT_MEMBERSHIP_COLUMNS,
  CHECKOUT_PRODUCT_COLUMNS,
  CHECKOUT_VOUCHER_COLUMNS
} from "../../constants";

export const EpicGetItemRelations: Epic<any, any, State> = (action$: ActionsObservable<any>, state$): any => action$
.ofType(
  CHECKOUT_ADD_ITEM,
  CHECKOUT_REMOVE_ITEM,
  CHECKOUT_ADD_CONTACT,
  CHECKOUT_REMOVE_CONTACT
).pipe(
  mergeMap(async () => {
    const cartItems = [];
    const suggestItems = [];

    if (!state$.value.checkout.contacts.length || !state$.value.checkout.items.length) {
      return { cartItems, suggestItems };
    }
    const relations = [];
    await state$.value.checkout.contacts
      .map(c => ({
          id: c.id,
          get: () => CheckoutService.getSaleRelations(
            state$.value.checkout.items.filter(i => i.type === "course" && !i.isRelation).map(i => i.courseId).toString() || null,
            state$.value.checkout.items.filter(i => i.type !== "course" && !i.isRelation).map(i => i.id).toString() || null,
            c.id
          )
      }))
      .reduce(async (a, b) => {
        await a;
        const draft = await b.get();
        const items = draft.filter((d: CheckoutSaleRelation) =>
          !state$.value.checkout.items.some(i => (i.type === "course" ? i.courseId === d.toItem.id : i.id === d.toItem.id)
          && relations.some(r => r.toItem.id !== d.toItem.id)));
        relations.push(...items.map(i => ({ ...i, contactId: b.id })));
      }, Promise.resolve());

    await relations
      .map(r => () => {
        switch (r.toItem.type) {
          case "Course": {
            let plainCourse;
            return EntityService.getPlainRecords("Course", "code,name,isTraineeship", `id is ${r.toItem.id}`)
              .then(res => {
                plainCourse = checkoutCourseMap(res.rows.map(getCustomColumnsMap("code,name,isTraineeship"))[0], true);
                return EntityService.getPlainRecords(
                  "CourseClass",
                  CHECKOUT_COURSE_CLASS_COLUMNS,
                  `course.id is ${r.toItem.id} and isActive is true`,
                  null,
                  0,
                  "startDateTime",
                  true
                );
              })
              .then(res => {
                if (res.rows.length) {
                  const plainClass = [res.rows[0]].map(checkoutCourseClassMap)[0];
                  r.toItem.cartItem = {
                    ...plainCourse,
                    courseId: plainCourse.id,
                    id: uniqid(),
                    price: plainClass.price,
                    discount: null,
                    discounts: [],
                    discountExTax: 0,
                    studyReason: "Not stated",
                    class: plainClass
                  };
                  r.toItem.link = `/course/${plainCourse.id}`;
                  r.toItem.cartItem.isRelation = true;
                }
              });
          }
          case "Membership": {
            return EntityService.getPlainRecords("MembershipProduct", CHECKOUT_MEMBERSHIP_COLUMNS, `id is ${r.toItem.id}`)
              .then(res => {
                r.toItem.cartItem = checkoutProductMap(res.rows.map(getCustomColumnsMap(CHECKOUT_MEMBERSHIP_COLUMNS))[0]);
                processCheckoutSale(r.toItem.cartItem, "membership");
                r.toItem.cartItem.isRelation = true;
                r.toItem.link = `/membership/${r.toItem.cartItem.id}`;
              });
          }
          case "Voucher": {
            return EntityService.getPlainRecords("VoucherProduct", CHECKOUT_VOUCHER_COLUMNS, `id is ${r.toItem.id}`)
              .then(res => {
                r.toItem.cartItem = checkoutVoucherMap(res.rows.map(getCustomColumnsMap(CHECKOUT_VOUCHER_COLUMNS))[0]);
                processCheckoutSale(r.toItem.cartItem, "voucher");
                r.toItem.cartItem.isRelation = true;
                r.toItem.link = `/voucher/${r.toItem.cartItem.id}`;
              });
          }
          case "Product": {
            return EntityService.getPlainRecords("ArticleProduct", CHECKOUT_PRODUCT_COLUMNS, `id is ${r.toItem.id}`)
              .then(res => {
                r.toItem.cartItem = checkoutProductMap(res.rows.map(getCustomColumnsMap(CHECKOUT_PRODUCT_COLUMNS))[0]);
                processCheckoutSale(r.toItem.cartItem, "product");
                r.toItem.cartItem.isRelation = true;
                r.toItem.link = `/product/${r.toItem.cartItem.id}`;
              });
          }
          default:
            return Promise.resolve();
        }
      })
      .reduce(async (a, b) => {
        await a;
        await b();
      }, Promise.resolve());

    relations.forEach(r => {
      switch (r.cartAction) {
        case "Add and allow removal":
        case "Add but do not allow removal":
          cartItems.push(r);
          break;
        case "Suggestion":
          suggestItems.push(r);
          break;
      }
    });

    return { cartItems, suggestItems };
  }),
  flatMap(data => [{
      type: CHECKOUT_UPDATE_RELATED_ITEMS,
      payload: data
    }
  ]),
  catchError(data => processError(data, "checkout/get/saleRelations", null, null))
);
