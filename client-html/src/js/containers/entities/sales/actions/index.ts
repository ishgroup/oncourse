import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import {
  ProductItem,
  ProductItemCancel,
  ProductItemStatus,
  ProductType,
  SaleType
} from "@api/model";

export const GET_SALES = _toRequestType("get/sales");
export const GET_SALES_FULFILLED = FULFILLED(GET_SALES);

export const GET_COURSE_CLASS_SALES = _toRequestType("get/courseClassSales");
export const GET_COURSE_CLASS_SALES_FULFILLED = FULFILLED(GET_COURSE_CLASS_SALES);

export const GET_SALE = _toRequestType("get/sale");
export const GET_SALE_FULFILLED = FULFILLED(GET_SALE);

export const UPDATE_SALE = _toRequestType("update/sale");
export const UPDATE_SALE_FULFILLED = FULFILLED(UPDATE_SALE);

export const SET_SALE_DELIVERED = _toRequestType("set/sale/delivered");

export const CANCEL_SALE = _toRequestType("cancel/sale");
export const CANCEL_SALE_FULFILLED = FULFILLED(CANCEL_SALE);

export const GET_SALE_DETAILS = _toRequestType("get/sale/details");
export const SET_SALE_DETAILS = "set/sale/details";

export const CLEAR_SALES = "clear/sales";

export const CLEAR_COURSE_CLASS_SALES = "clear/courseClassSales";

export const setSaleDelivered = (id: number) => ({
  type: SET_SALE_DELIVERED,
  payload: { id }
});

export const getSale = (id: string) => ({
  type: GET_SALE,
  payload: { id }
});

export const getSaleDetails = (id: string) => ({
  type: GET_SALE_DETAILS,
  payload: { id }
});

export const setSaleDetails = (selectedSaleStatus: ProductItemStatus, selectedSaleType: ProductType) => ({
  type: SET_SALE_DETAILS,
  payload: { selectedSaleStatus, selectedSaleType }
});

export const getSales = (search: string, entities: SaleType[] = ["Product", "Membership", "Voucher"]) => ({
  type: GET_SALES,
  payload: { search, entities }
});

export const getCourseClassSales = (search: string) => ({
  type: GET_COURSE_CLASS_SALES,
  payload: search
});

export const clearSales = pending => ({
  type: CLEAR_SALES,
  payload: { pending, items: null }
});

export const clearCourseClassSales = pending => ({
  type: CLEAR_COURSE_CLASS_SALES,
  payload: { pending, courseClassItems: null }
});

export const updateSale = (id: string, productItem: ProductItem) => ({
  type: UPDATE_SALE,
  payload: { id, productItem }
});

export const cancelSale = (productItemCancel: ProductItemCancel) => ({
  type: CANCEL_SALE,
  payload: { productItemCancel }
});
