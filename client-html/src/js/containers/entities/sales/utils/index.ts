import { Category, ProductItem, ProductType } from "@api/model";
import { getMainRouteUrl } from "../../../../routes/routesMapping";

export const buildUrl = (id: number | string, category: Category) => getMainRouteUrl(category) + `/${id}`;

export const productUrl = (productItem: ProductItem) => {
  switch (productItem.productType) {
    case ProductType.Membership:
      return buildUrl(productItem.productId, "Memberships");
    case ProductType.Voucher:
      return buildUrl(productItem.productId, "Voucher Types");
    default:
      return undefined;
  }
};

export const getProductAqlType = (type: ProductType | string) => {
  switch (type) {
    case ProductType.Membership:
      return "MembershipProduct";
    case ProductType.Voucher:
      return "VoucherProduct";
    case ProductType.Product:
      return "ArticleProduct";
    default:
      throw Error("Unknown product type");
  }
};
