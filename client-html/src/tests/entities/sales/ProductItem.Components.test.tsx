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
      const inputs = wrapper.find("input");
      expect(inputs[0].attribs.value).toContain(initialValues.purchasedByName);
      expect(inputs[1].attribs.value).toContain(format(new Date(initialValues.purchasedOn), EEE_D_MMM_YYYY).toString());
      expect(inputs[2].attribs.value).toContain(initialValues.purchasePrice);
      expect(inputs[3].attribs.value).toContain(initialValues.status);
    }
  });
});
