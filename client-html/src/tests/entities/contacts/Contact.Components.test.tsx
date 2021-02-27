import { mockedEditView } from "../../common/MockedEditView.Components";
import ContactEditView from "../../../js/containers/entities/contacts/components/ContactEditView";

describe("Virtual rendered ContactEditView", () => {
  mockedEditView({
    entity: "Contact",
    EditView: ContactEditView,
    record: mockecApi => mockecApi.db.getContact(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#title").text()).toContain("No value");
      expect(wrapper.find("#firstName").text()).toContain(initialValues.firstName);
      expect(wrapper.find("#middleName").text()).toContain("No value");
      expect(wrapper.find("#lastName").text()).toContain(initialValues.lastName);
      expect(wrapper.find("#street").text()).toContain("No value");
      expect(wrapper.find("#suburb").text()).toContain("No value");
      expect(wrapper.find("#state").text()).toContain("No value");
      expect(wrapper.find("#postcode").text()).toContain("No value");
      expect(wrapper.find("#country").text()).toContain("No value");
      expect(wrapper.find("#mobilePhone").text()).toContain("No value");
      expect(wrapper.find("#email").text()).toContain(initialValues.email);
      expect(wrapper.find("#message").text()).toContain("No value");
      expect(wrapper.find("#homePhone").text()).toContain("No value");
      expect(wrapper.find("#workPhone").text()).toContain("No value");
      expect(wrapper.find("#fax").text()).toContain("No value");
      expect(wrapper.find("#abn").text()).toContain("No value");
      expect(wrapper.find("#birthDate").text()).toContain("No value");
      expect(wrapper.find("#gender").text()).toContain("Not stated");
      expect(wrapper.find("#honorific").text()).toContain("No value");

      wrapper.find("#Financial .centeredFlex button").simulate("click");
      expect(wrapper.find("#taxId").text()).toContain("No value");
      expect(wrapper.find("#invoiceTerms").text()).toContain("No value");

      wrapper.find("#VET .centeredFlex button").simulate("click");
      expect(wrapper.find("div[id=\"student.countryOfBirth\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"student.townOfBirth\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"student.indigenousStatus\"]").text()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.language\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"student.englishProficiency\"]").text()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.highestSchoolLevel\"]").text()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.yearSchoolCompleted\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"student.priorEducationCode\"]").text()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.labourForceStatus\"]").text()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.isStillAtSchool\"]").text()).toContain("No");
      expect(wrapper.find("div[id=\"student.disabilityType\"]").text()).toContain("Not stated");
      expect(wrapper.find("#VET input[type=\"checkbox\"]").at(0).props().checked).toEqual(false);
      expect(wrapper.find("div[id=\"student.clientIndustryEmployment\"]").text()).toContain("Not Stated");
      expect(wrapper.find("div[id=\"student.clientOccupationIdentifier\"]").text()).toContain("Not Stated");
      expect(wrapper.find("div[id=\"student.uniqueLearnerIdentifier\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"student.chessn\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"student.usi\"]").text()).toContain("No value");
      expect(wrapper.find("#VET input[type=\"checkbox\"]").at(1).props().checked).toEqual(false);
      expect(wrapper.find("#VET input[type=\"checkbox\"]").at(2).props().checked).toEqual(false);

      expect(wrapper.find("#Resume").text()).toContain(initialValues.tutor.resume);
      expect(wrapper.find("div[id=\"tutor.givenNameLegal\"]").text()).toContain("FirstName");
      expect(wrapper.find("div[id=\"tutor.familyNameLegal\"]").text()).toContain("LastName");
      expect(wrapper.find("#tfn").text()).toContain("No value");
      expect(wrapper.find("div[id=\"tutor.payrollRef\"]").text()).toContain(initialValues.tutor.payrollRef);
      expect(wrapper.find("div[id=\"tutor.dateStarted\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"tutor.dateFinished\"]").text()).toContain("No value");
      expect(wrapper.find("#Tutor input[type=\"checkbox\"]").props().checked).toEqual(false);
      expect(wrapper.find("div[id=\"tutor.wwChildrenRef\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"tutor.wwChildrenStatus\"]").text()).toContain(initialValues.tutor.wwChildrenStatus);
      expect(wrapper.find("div[id=\"tutor.wwChildrenExpiry\"]").text()).toContain("No value");
      expect(wrapper.find("div[id=\"tutor.wwChildrenCheckedOn\"]").text()).toContain("No value");
    }
  });
});
