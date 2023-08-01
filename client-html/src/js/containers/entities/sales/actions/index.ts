import { ProductItemCancel, ProductItemStatus, ProductType, SaleType } from "@api/model";
import { _toRequestType, FULFILLED, REJECTED } from "../../../../common/actions/ActionUtils";

export const GET_SALES = _toRequestType("get/sales");
export const GET_SALES_FULFILLED = FULFILLED(GET_SALES);
export const GET_SALES_REJECTED = REJECTED(GET_SALES);

export const SET_SALE_DELIVERED = _toRequestType("set/sale/delivered");

export const CANCEL_SALE = _toRequestType("cancel/sale");
export const CANCEL_SALE_FULFILLED = FULFILLED(CANCEL_SALE);

export const GET_SALE_DETAILS = _toRequestType("get/sale/details");
export const SET_SALE_DETAILS = "set/sale/details";

export const CLEAR_SALES = "clear/sales";

export const CLEAR_COURSE_CLASS_SALES = "clear/courseClassSales";

export const GET_SALE_MENU_TAGS = "get/sale/tags";

export const setSaleDelivered = (id: number) => ({
  type: SET_SALE_DELIVERED,
  payload: { id }
});

export const getSalesManuTags = () => ({
  type: GET_SALE_MENU_TAGS,
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

export const clearSales = pending => ({
  type: CLEAR_SALES,
  payload: { pending, items: null }
});

export const getSalesRejected = () => ({
  type: GET_SALES_REJECTED,
});

export const cancelSale = (productItemCancel: ProductItemCancel) => ({
  type: CANCEL_SALE,
  payload: { productItemCancel }
});