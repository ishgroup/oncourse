import { format } from "date-fns";
import { EEE_D_MMM_YYYY } from "../../../js/common/utils/dates/format";
import SalesEditView from "../../../js/containers/entities/sales/components/SalesEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered SalesEditView", () => {
  mockedEditView({
    entity: "ProductItem",
    EditView: SalesEditView,
    record: mockecApi => mockecApi.db.getProductItem(1),
    render: (wrapper, initialValues) => {
      const text = wrapper.find(".textField").text();
      expect(text).toContain(initialValues.productName);
      expect(text).toContain(initialValues.purchasedByName);
      expect(text).toContain(
        "Purchased on" + format(new Date(initialValues.purchasedOn), EEE_D_MMM_YYYY).toString()
      );
      expect(text).toContain(initialValues.purchasePrice);
      expect(text).toContain(initialValues.status);
    }
  });
});
