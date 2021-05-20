import MembershipProductEditView from "../../../js/containers/entities/membershipProducts/components/MembershipProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered MembershipProductEditView", () => {
  mockedEditView({
    entity: "MembershipProduct",
    EditView: MembershipProductEditView,
    record: mockecApi => mockecApi.db.getMembershipProduct(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name").text()).toContain(initialValues.name);
      expect(wrapper.find("#code").text()).toContain(initialValues.code);
      expect(wrapper.find("#description").text()).toContain(initialValues.description);
      expect(wrapper.find("#incomeAccountId").text()).toContain("No value");
      expect(wrapper.find("#feeExTax").text()).toContain(initialValues.feeExTax);
      expect(wrapper.find("#totalFee").text()).toContain(initialValues.totalFee);
      expect(wrapper.find("#taxId").text()).toContain("No value");
      expect(wrapper.find("#status").text()).toContain(initialValues.status);
    }
  });
});
