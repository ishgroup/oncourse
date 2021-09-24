import { mockedEditView } from "../../common/MockedEditView.Components";
import ContactEditView from "../../../js/containers/entities/contacts/components/ContactEditView";

describe("Virtual rendered ContactEditView", () => {
  mockedEditView({
    entity: "Contact",
    EditView: ContactEditView,
    record: mockecApi => mockecApi.db.getContact(1),
    render: (wrapper, initialValues, shallow) => {
      expect(wrapper.find("#title input")).not.toBeNull();
      expect(wrapper.find("#firstName input").val()).toContain(initialValues.firstName);
      expect(wrapper.find("#middleName input").val()).not.toBeNull();
      expect(wrapper.find("#lastName input").val()).toContain(initialValues.lastName);
      expect(wrapper.find("#street input").val()).not.toBeNull();
      expect(wrapper.find("#suburb input").val()).not.toBeNull();
      expect(wrapper.find("#state input").val()).not.toBeNull();
      expect(wrapper.find("#postcode input").val()).not.toBeNull();
      expect(wrapper.find("#country input").val()).not.toBeNull();
      expect(wrapper.find("#mobilePhone input").val()).not.toBeNull();
      expect(wrapper.find("#email input").val()).toContain(initialValues.email);
      expect(wrapper.find("#message input").val()).not.toBeNull();
      expect(wrapper.find("#homePhone input").val()).not.toBeNull();
      expect(wrapper.find("#workPhone input").val()).not.toBeNull();
      expect(wrapper.find("#fax input").val()).not.toBeNull();
      expect(wrapper.find("#abn input").val()).not.toBeNull();
      expect(wrapper.find("#birthDate input").val()).not.toBeNull();
      expect(wrapper.find("#gender input").val()).not.toBeNull();
      expect(wrapper.find("#honorific input").val()).not.toBeNull();

      shallow.find("#Financial .centeredFlex button").simulate("click");
      expect(wrapper.find("#taxId input").val()).not.toBeNull();
      expect(wrapper.find("#invoiceTerms input").val()).not.toBeNull();

      shallow.find("#VET .centeredFlex button").simulate("click");
      expect(wrapper.find("div[id=\"student.countryOfBirth\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.townOfBirth\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.indigenousStatus\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.language\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.englishProficiency\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.highestSchoolLevel\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.yearSchoolCompleted\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.priorEducationCode\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.labourForceStatus\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.isStillAtSchool\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.disabilityType\"] input").val()).not.toBeNull();
      expect(shallow.find("#VET input[type=\"checkbox\"]").at(0).props().checked).toEqual(false);
      expect(wrapper.find("div[id=\"student.clientIndustryEmployment\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.clientOccupationIdentifier\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.uniqueLearnerIdentifier\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.chessn\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"student.usi\"] input").val()).not.toBeNull();
      expect(shallow.find("#VET input[type=\"checkbox\"]").at(1).props().checked).toEqual(false);
      expect(shallow.find("#VET input[type=\"checkbox\"]").at(2).props().checked).toEqual(false);
      expect(wrapper.find("#Resume").text()).toContain(initialValues.tutor.resume);
      expect(shallow.find("div[id=\"tutor.givenNameLegal\"] input").props().placeholder).toContain("FirstName");
      expect(shallow.find("div[id=\"tutor.familyNameLegal\"] input").props().placeholder).toContain("LastName");
      expect(wrapper.find("#tfn input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"tutor.payrollRef\"] input").val()).toContain(initialValues.tutor.payrollRef);
      expect(wrapper.find("div[id=\"tutor.dateStarted\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"tutor.dateFinished\"] input").val()).not.toBeNull();
      expect(shallow.find("#Tutor input[type=\"checkbox\"]").props().checked).toEqual(false);
      expect(wrapper.find("div[id=\"tutor.wwChildrenRef\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"tutor.wwChildrenStatus\"] input").val()).toContain(initialValues.tutor.wwChildrenStatus);
      expect(wrapper.find("div[id=\"tutor.wwChildrenExpiry\"] input").val()).not.toBeNull();
      expect(wrapper.find("div[id=\"tutor.wwChildrenCheckedOn\"] input").val()).not.toBeNull();
    }
  });
});
