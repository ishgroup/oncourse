import { mockedEditView } from "../../common/MockedEditView.Components";
import ContactEditView from "../../../js/containers/entities/contacts/components/ContactEditView";

describe("Virtual rendered ContactEditView", () => {
  mockedEditView({
    entity: "Contact",
    EditView: ContactEditView,
    record: mockecApi => mockecApi.db.getContact(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#title input").val()).toContain("No value");
      expect(wrapper.find("#firstName input").val()).toContain(initialValues.firstName);
      expect(wrapper.find("#middleName input").val()).toContain("No value");
      expect(wrapper.find("#lastName input").val()).toContain(initialValues.lastName);
      expect(wrapper.find("#street input").val()).toContain("No value");
      expect(wrapper.find("#suburb input").val()).toContain("No value");
      expect(wrapper.find("#state input").val()).toContain("No value");
      expect(wrapper.find("#postcode input").val()).toContain("No value");
      expect(wrapper.find("#country input").val()).toContain("No value");
      expect(wrapper.find("#mobilePhone input").val()).toContain("No value");
      expect(wrapper.find("#email input").val()).toContain(initialValues.email);
      expect(wrapper.find("#message input").val()).toContain("No value");
      expect(wrapper.find("#homePhone input").val()).toContain("No value");
      expect(wrapper.find("#workPhone input").val()).toContain("No value");
      expect(wrapper.find("#fax input").val()).toContain("No value");
      expect(wrapper.find("#abn input").val()).toContain("No value");
      expect(wrapper.find("#birthDate input").val()).toContain("No value");
      expect(wrapper.find("#gender input").val()).toContain("Not stated");
      expect(wrapper.find("#honorific input").val()).toContain("No value");

      wrapper.find("#Financial .centeredFlex button").simulate("click");
      expect(wrapper.find("#taxId input").val()).toContain("No value");
      expect(wrapper.find("#invoiceTerms input").val()).toContain("No value");

      wrapper.find("#VET .centeredFlex button").simulate("click");
      expect(wrapper.find("div[id=\"student.countryOfBirth\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"student.townOfBirth\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"student.indigenousStatus\"] input").val()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.language\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"student.englishProficiency\"] input").val()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.highestSchoolLevel\"] input").val()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.yearSchoolCompleted\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"student.priorEducationCode\"] input").val()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.labourForceStatus\"] input").val()).toContain("Not stated");
      expect(wrapper.find("div[id=\"student.isStillAtSchool\"] input").val()).toContain("No");
      expect(wrapper.find("div[id=\"student.disabilityType\"] input").val()).toContain("Not stated");
      expect(wrapper.find("#VET input[type=\"checkbox\"]").at(0).props().checked).toEqual(false);
      expect(wrapper.find("div[id=\"student.clientIndustryEmployment\"] input").val()).toContain("Not Stated");
      expect(wrapper.find("div[id=\"student.clientOccupationIdentifier\"] input").val()).toContain("Not Stated");
      expect(wrapper.find("div[id=\"student.uniqueLearnerIdentifier\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"student.chessn\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"student.usi\"] input").val()).toContain("No value");
      expect(wrapper.find("#VET input[type=\"checkbox\"]").at(1).props().checked).toEqual(false);
      expect(wrapper.find("#VET input[type=\"checkbox\"]").at(2).props().checked).toEqual(false);

      expect(wrapper.find("#Resume input").val()).toContain(initialValues.tutor.resume);
      expect(wrapper.find("div[id=\"tutor.givenNameLegal\"] input").val()).toContain("FirstName");
      expect(wrapper.find("div[id=\"tutor.familyNameLegal\"] input").val()).toContain("LastName");
      expect(wrapper.find("#tfn input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"tutor.payrollRef\"] input").val()).toContain(initialValues.tutor.payrollRef);
      expect(wrapper.find("div[id=\"tutor.dateStarted\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"tutor.dateFinished\"] input").val()).toContain("No value");
      expect(wrapper.find("#Tutor input[type=\"checkbox\"]").props().checked).toEqual(false);
      expect(wrapper.find("div[id=\"tutor.wwChildrenRef\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"tutor.wwChildrenStatus\"] input").val()).toContain(initialValues.tutor.wwChildrenStatus);
      expect(wrapper.find("div[id=\"tutor.wwChildrenExpiry\"] input").val()).toContain("No value");
      expect(wrapper.find("div[id=\"tutor.wwChildrenCheckedOn\"] input").val()).toContain("No value");
    }
  });
});
