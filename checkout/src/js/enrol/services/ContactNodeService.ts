import * as models from "../../model";
import {inspect} from "util";
import {Actions} from "../../web/actions/Actions";

export class ContactNodeService {
  /**
   * Extracts correspondent item from the node and cast to corespondent type.
   * We do it to get an possibility to use 'instanceof' expression for purchase items.
   * className can accept the following values:
   */
  static getPurchaseItem = (node: models.ContactNode, original: any): models.PurchaseItem => {

    if (original instanceof models.Application) {
      return Object.assign(new models.Application(), node.applications[0]);
    }

    if (original instanceof models.Article) {
      return Object.assign(new models.Article(), node.articles[0]);
    }

    if (original instanceof models.Enrolment) {
      return Object.assign(new models.Enrolment(), node.enrolments[0]);
    }

    if (original instanceof models.Membership) {
      return Object.assign(new models.Membership(), node.memberships[0]);
    }

    if (original instanceof models.Voucher) {
      return Object.assign(new models.Voucher(), node.vouchers[0]);
    }

    if (original instanceof models.WaitingList) {
      return Object.assign(new models.WaitingList(), node.waitingLists[0]);
    }

    throw new Error(`Unsupported purchase item: ${inspect(original, true, 10, true)}`);
  }

  static getRelationsUpdateActions = (nodes: models.ContactNode[]) => {
    const actions = [];
    nodes.forEach(node => {
      if (node.articles.length) {
        node.articles.forEach(article => {
          if (article.relatedClassId || article.relatedProductId) {
            actions.push({
              type: Actions.REQUEST_PRODUCT,
              payload: article.productId,
            })
          }
        })
      }
      if (node.vouchers.length) {
        node.vouchers.forEach(voucher => {
          if (voucher.relatedClassId || voucher.relatedProductId) {
            actions.push({
              type: Actions.REQUEST_PRODUCT,
              payload: voucher.productId,
            })
          }
        })
      }
      if (node.memberships.length) {
        node.memberships.forEach(membership => {
          if (membership.relatedClassId || membership.relatedProductId) {
            actions.push({
              type: Actions.REQUEST_PRODUCT,
              payload: membership.productId,
            })
          }
        })
      }
      if (node.enrolments.length) {
        node.enrolments.forEach(enrolment => {
          if (enrolment.relatedClassId || enrolment.relatedProductId) {
            if(!enrolment.classId) {
              actions.push({
                type: Actions.REQUEST_INACTIVE_COURSE,
                payload: enrolment.courseId,
              })
            } else {
              actions.push({
                type: Actions.REQUEST_COURSE_CLASS,
                payload: enrolment.classId,
              })
            }
          }
        })
      }
    })
    return actions;
  }
}
