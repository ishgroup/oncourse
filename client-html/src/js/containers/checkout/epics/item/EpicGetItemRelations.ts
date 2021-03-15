import { ActionsObservable, Epic } from "redux-observable";
import {
  flatMap, catchError, mergeMap
} from "rxjs/operators";
import { CheckoutSaleRelation } from "@api/model";

import { closestIndexTo } from "date-fns";
import { concat, from } from "rxjs";
import {
  CHECKOUT_ADD_CONTACT,
  CHECKOUT_ADD_ITEM,
  CHECKOUT_REMOVE_CONTACT,
  CHECKOUT_UPDATE_RELATED_ITEMS
} from "../../actions";
import CheckoutService from "../../services/CheckoutService";
import { State } from "../../../../reducers/state";
import { processError } from "../../../../common/epics/EpicUtils";
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
import { checkoutUpdateSummaryClassesDiscounts } from "../../actions/checkoutSummary";
import { FETCH_FINISH, FETCH_START } from "../../../../common/actions";
import uniqid from "../../../../common/utils/uniqid";

const assignTypeProps = r => {
  r.toItem.cartItem.cartAction = r.cartAction;
  r.toItem.cartItem.fromItemRelation = r.fromItem;
  r.toItem.cartItem.fromItemRelation.type = r.fromItem.type.toLowerCase();
  r.toItem.cartItem.relationDiscount = r.discount;
  r.toItem.cartItem.checked = true;
  r.toItem.type = r.toItem.cartItem.fromItemRelation.type;
};

export const EpicGetItemRelations: Epic<any, any, State> = (action$: ActionsObservable<any>, state$): any => action$
.ofType(
  CHECKOUT_ADD_ITEM,
  CHECKOUT_ADD_CONTACT,
  CHECKOUT_REMOVE_CONTACT
).pipe(
    mergeMap(sourceAction =>
      concat(
        [
          {
            type: FETCH_START,
            payload: {
              hideIndicator: false
            }
          }
        ],
        from((async () => {
          if (sourceAction.type === CHECKOUT_ADD_ITEM && sourceAction.payload.item.cartAction) {
            return null;
          }

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
                state$.value.checkout.items.filter(i => i.type === "course" && !i.cartAction).map(i => i.courseId).toString() || null,
                state$.value.checkout.items.filter(i => i.type !== "course" && !i.cartAction).map(i => i.id).toString() || null,
                c.id
              )
            }))
            .reduce(async (a, b) => {
              await a;
              const draft = await b.get();
              const items = draft.filter((d: CheckoutSaleRelation) =>
                !state$.value.checkout.items.some(i => (i.type === "course" ? i.courseId === d.toItem.id : i.id === d.toItem.id)));

              const newItems = [];

              items.forEach(i => {
                const match = relations.find(r => r.toItem.id === i.toItem.id);
                if (match) {
                  match.contactIds.push(b.id);
                } else {
                  newItems.push({ ...i, contactIds: [b.id] });
                }
              });
              relations.push(...newItems);
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
                        `course.id is ${r.toItem.id} and isCancelled is false and isActive is true`,
                        null,
                        0,
                        "startDateTime",
                        true
                      );
                    })
                    .then(res => {
                      if (res.rows.length) {
                        const classIndex = closestIndexTo(new Date(), res.rows.map(r => new Date(r.values[4])));
                        const plainClass = [res.rows[classIndex]].map(checkoutCourseClassMap)[0];

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
                        assignTypeProps(r);
                        r.toItem.link = `/course/${plainCourse.id}`;
                      } else {
                        r.cartAction = null;
                      }
                    });
                }
                case "Membership": {
                  return EntityService.getPlainRecords("MembershipProduct", CHECKOUT_MEMBERSHIP_COLUMNS, `id is ${r.toItem.id}`)
                    .then(res => {
                      r.toItem.cartItem = checkoutProductMap(res.rows.map(getCustomColumnsMap(CHECKOUT_MEMBERSHIP_COLUMNS))[0]);
                      processCheckoutSale(r.toItem.cartItem, "membership");
                      assignTypeProps(r);
                      r.toItem.link = `/membership/${r.toItem.cartItem.id}`;
                    });
                }
                case "Voucher": {
                  return EntityService.getPlainRecords("VoucherProduct", CHECKOUT_VOUCHER_COLUMNS, `id is ${r.toItem.id}`)
                    .then(res => {
                      r.toItem.cartItem = checkoutVoucherMap(res.rows.map(getCustomColumnsMap(CHECKOUT_VOUCHER_COLUMNS))[0]);
                      processCheckoutSale(r.toItem.cartItem, "voucher");
                      assignTypeProps(r);
                      r.toItem.link = `/voucher/${r.toItem.cartItem.id}`;
                    });
                }
                case "Product": {
                  return EntityService.getPlainRecords("ArticleProduct", CHECKOUT_PRODUCT_COLUMNS, `id is ${r.toItem.id}`)
                    .then(res => {
                      r.toItem.cartItem = checkoutProductMap(res.rows.map(getCustomColumnsMap(CHECKOUT_PRODUCT_COLUMNS))[0]);
                      processCheckoutSale(r.toItem.cartItem, "product");
                      assignTypeProps(r);
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
        })()).pipe(
          flatMap(data => (data ? [{
            type: CHECKOUT_UPDATE_RELATED_ITEMS,
            payload: data
          },
            checkoutUpdateSummaryClassesDiscounts()
          ] : [])),
          catchError(data => processError(data, "checkout/get/saleRelations", null, null))
        ),
        [
          {
            type: FETCH_FINISH
          }
        ]
      ))

);
