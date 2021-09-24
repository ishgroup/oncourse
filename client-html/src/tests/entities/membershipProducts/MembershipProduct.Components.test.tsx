import MembershipProductEditView from "../../../js/containers/entities/membershipProducts/components/MembershipProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered MembershipProductEditView", () => {
  mockedEditView({
    entity: "MembershipProduct",
    EditView: MembershipProductEditView,
    record: mockecApi => mockecApi.db.getMembershipProduct(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find("#code input").val()).toContain(initialValues.code);
      expect(wrapper.find("#description input").val()).toContain(initialValues.description);
      expect(wrapper.find("#incomeAccountId input").val()).toContain("No value");
      expect(wrapper.find("#feeExTax input").val()).toContain(initialValues.feeExTax);
      expect(wrapper.find("#totalFee input").val()).toContain(initialValues.totalFee);
      expect(wrapper.find("#taxId input").val()).toContain("No value");
      expect(wrapper.find("#status input").val()).toContain(initialValues.status);
    }
  });
});
