import { format } from "date-fns";
import { mockedEditView } from "../../common/MockedEditView.Components";
import ContactEditView from "../../../js/containers/entities/contacts/components/ContactEditView";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";

describe("Virtual rendered ContactEditView", () => {
  mockedEditView({
    entity: "Contact",
    EditView: ContactEditView,
    record: mockecApi => mockecApi.db.getContact(1),
    render: ({
      screen, initialValues, formRoleName, fireEvent
    }) => {
      fireEvent.click(screen.getByTestId("expand-button-2"));
      fireEvent.click(screen.getByTestId("expand-button-7"));
      fireEvent.click(screen.getByTestId("expand-button-8"));

      const contactFormData = {
        title: initialValues.title,
        firstName: initialValues.firstName,
        middleName: initialValues.middleName,
        lastName: initialValues.lastName,
        street: initialValues.street,
        suburb: initialValues.suburb,
        state: initialValues.state,
        postcode: initialValues.postcode,
        country: initialValues.country,
        mobilePhone: initialValues.mobilePhone,
        email: initialValues.email,
        message: initialValues.message,
        homePhone: initialValues.homePhone,
        workPhone: initialValues.workPhone,
        fax: initialValues.fax,
        abn: initialValues.abn,
        birthDate: format(new Date(initialValues.birthDate), III_DD_MMM_YYYY),
        gender: initialValues.gender,
        honorific: initialValues.honorific,

        taxId: initialValues.taxId,
        invoiceTerms: initialValues.invoiceTerms,

        "student.countryOfBirth": initialValues.student.countryOfBirth,
        "student.townOfBirth": initialValues.student.townOfBirth,
        "student.indigenousStatus": initialValues.student.indigenousStatus,
        "student.language": initialValues.student.language,
        "student.englishProficiency": initialValues.student.englishProficiency,
        "student.highestSchoolLevel": initialValues.student.highestSchoolLevel,
        "student.yearSchoolCompleted": initialValues.student.yearSchoolCompleted,
        "student.priorEducationCode": initialValues.student.priorEducationCode,
        "student.labourForceStatus": initialValues.student.labourForceStatus,
        "student.disabilityType": initialValues.student.disabilityType,
        "student.specialNeedsAssistance": initialValues.student.specialNeedsAssistance,
        "student.clientIndustryEmployment": initialValues.student.clientIndustryEmployment,
        "student.clientOccupationIdentifier": initialValues.student.clientOccupationIdentifier,
        "student.uniqueLearnerIdentifier": initialValues.student.uniqueLearnerIdentifier,
        "student.chessn": initialValues.student.chessn,
        "student.usi": initialValues.student.usi,

        "tutor.givenNameLegal": initialValues.tutor.givenNameLegal,
        "tutor.familyNameLegal": initialValues.tutor.familyNameLegal,
        "tutor.payrollRef": initialValues.tutor.payrollRef,
        "tutor.dateStarted": format(new Date(initialValues.tutor.dateStarted), III_DD_MMM_YYYY),
        "tutor.dateFinished": format(new Date(initialValues.tutor.dateFinished), III_DD_MMM_YYYY),
        "tutor.wwChildrenRef": initialValues.tutor.wwChildrenRef,
        "tutor.wwChildrenStatus": initialValues.tutor.wwChildrenStatus,
        "tutor.wwChildrenExpiry": format(new Date(initialValues.tutor.wwChildrenExpiry), III_DD_MMM_YYYY),
        "tutor.wwChildrenCheckedOn": format(new Date(initialValues.tutor.wwChildrenCheckedOn), III_DD_MMM_YYYY),
      };

      expect(screen.getByRole(formRoleName)).toHaveFormValues(contactFormData);
    }
  });
});
