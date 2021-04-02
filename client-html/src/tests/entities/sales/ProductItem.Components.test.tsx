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
      expect(wrapper.find(".textField").at(0).text()).toContain(initialValues.productName);
      expect(wrapper.find(".textField").at(1).text()).toContain(initialValues.purchasedByName);

      expect(wrapper.find(".textField").at(2).text()).toContain(
        "Purchased on" + format(new Date(initialValues.purchasedOn), EEE_D_MMM_YYYY).toString()
      );

      expect(wrapper.find(".textField").at(3).text()).toContain(initialValues.purchasePrice);
      expect(wrapper.find(".textField").at(4).text()).toContain(initialValues.status);
    }
  });
});
