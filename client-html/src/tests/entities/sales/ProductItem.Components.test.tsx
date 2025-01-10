import { format } from "date-fns";
import { EEE_D_MMM_YYYY } from "ish-ui";
import SalesEditView from "../../../js/containers/entities/sales/components/SalesEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered SalesEditView", () => {
  mockedEditView({
    entity: "ProductItem",
    EditView: SalesEditView,
    record: mockecApi => mockecApi.db.getProductItem(1),
    render: ({ screen, initialValues }) => {
      expect(screen.getByLabelText('Purchased by', { selector: 'input' }).value).toBe(initialValues.purchasedByName);
      expect(screen.getByLabelText('Purchased on').value).toBe(format(new Date(initialValues.purchasedOn), EEE_D_MMM_YYYY).toString());
      expect(screen.getByLabelText('Purchase price').value).toBe(initialValues.purchasePrice.toFixed(2));
      expect(screen.getByLabelText('Status').value).toBe(initialValues.status);
    }
  });
});
