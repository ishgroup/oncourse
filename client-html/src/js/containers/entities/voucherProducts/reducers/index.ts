/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course, VoucherProduct } from "@api/model";
import { VoucherProductState } from "./state";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_PLAIN_VOUCHER_PRODUCTS_SEARCH,
  CLEAR_VOUCHER_PRODUCT_COURSES,
  CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE,
  GET_PLAIN_VOUCHER_PRODUCTS,
  GET_PLAIN_VOUCHER_PRODUCTS_FULFILLED,
  GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED,
  SEARCH_VOUCHER_PRODUCT_COURSES_FULFILLED,
  SET_PLAIN_VOUCHER_PRODUCTS_SEARCH
} from "../actions";

class VoucherProductStateClass implements VoucherProductState {
  maxFee: number = 0;

  minFee: number = 0;

  foundCourses: Course[] = [];

  pendingCourses: boolean = false;

  items: VoucherProduct[] = [];

  search: string = "";

  loading: boolean = false;

  rowsCount: number = 5000;
}

export const voucherProductReducer = (
  state: VoucherProductState = new VoucherProductStateClass(),
  action: IAction<any>
): any => {
  switch (action.type) {
    case SEARCH_VOUCHER_PRODUCT_COURSES_FULFILLED:
    case GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_VOUCHER_PRODUCT_COURSES: {
      return {
        ...state,
        foundCourses: [],
        pendingCourses: action.payload
      };
    }

    case CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE: {
      return {
        ...state,
        maxFee: 0,
        minFee: 0
      };
    }

    case GET_PLAIN_VOUCHER_PRODUCTS: {
      return {
        ...state,
        loading: true
      };
    }

    case SET_PLAIN_VOUCHER_PRODUCTS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_PLAIN_VOUCHER_PRODUCTS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_PLAIN_VOUCHER_PRODUCTS_FULFILLED: {
      const { items, offset, pageSize } = action.payload;
      const updated = offset ? state.items.concat(items) : items;
      return {
        ...state,
        loading: false,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    default:
      return state;
  }
};
