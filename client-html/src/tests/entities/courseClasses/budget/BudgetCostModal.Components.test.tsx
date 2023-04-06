import * as React from "react";
import { addDays, format } from "date-fns";
import { defaultComponents } from "../../../common/Default.Components";
import { D_MMM_YYYY } from "../../../../js/common/utils/dates/format";
import BudgetCostModal from "../../../../js/containers/entities/courseClasses/components/budget/modal/BudgetCostModal";
import { getCustomColumnsMap } from "../../../../js/common/utils/common";
import { formatCurrency } from "../../../../js/common/utils/numbers/numbersNormalizing";
import { getClassFeeTotal } from "../../../../js/containers/entities/courseClasses/components/budget/utils";
import { COURSE_CLASS_COST_DIALOG_FORM } from "../../../../js/containers/entities/courseClasses/constants";

describe("Virtual rendered BudgetCostModal of Class edit view", () => {
  defaultComponents({
    entity: "BudgetCostModal",
    View: props => <BudgetCostModal {...props} />,
    record: mockedApi => mockedApi.db.getCourseClassBudget().find(bv => bv.id === 7257),
    defaultProps: ({ mockedApi }) => ({
      classFee: mockedApi.db.getCourseClassTimetable(),
      currentTax: getClassFeeTotal(mockedApi.db.getCourseClassBudget("1"), mockedApi.db.getPlainTaxesFormatted()),
      taxes: mockedApi.db.getPlainTaxesFormatted(),
      classValues: {
        ...mockedApi.db.getCourseClass(1),
        trainingPlan: mockedApi.db.getCourseClassTrainingPlan(),
        sessions: mockedApi.db.getCourseClassTimetable(),
        tutors: mockedApi.db.getCourseClassTutors(1),
        budget: mockedApi.db.getCourseClassBudget("1"),
        studentAttendance: mockedApi.db.getCourseClassAttendanceStudents(),
        notes: [],
        assessments: mockedApi.db.getCourseClassAssessment(1),
      },
      currencySymbol: "$",
      onClose: jest.fn(),
      onSave: jest.fn(),
      onSubmit: jest.fn(),
      form: COURSE_CLASS_COST_DIALOG_FORM,
    }),
    state: ({ mockedApi }) => ({
      preferences: { tutorRoles: mockedApi.db.getPlainTutorRoles().rows.map(getCustomColumnsMap("name,description,active,currentPayrate.oncostRate,currentPayrate.rate,currentPayrate.type")) },
      plainSearchRecords: {
        Account: { items: mockedApi.db.getPlainAccounts().rows.map(getCustomColumnsMap("description,accountCode,type,tax.id")) }
      },
      courseClass: {
        budgetModalOpened: true,
        defaultOnCostRate: undefined,
      },
    }),
    render: ({ screen, initialValues, viewProps }) => {
      expect(screen.getByLabelText("On enrolment").value).toBe(formatCurrency(initialValues.perUnitAmountIncTax, "").toString());
      expect(screen.getByLabelText("Invoice line title").value).toBe(initialValues.description);

      const paymentPlansRecord = initialValues.paymentPlan;
      const classStart = viewProps.classValues.startDateTime;

      paymentPlansRecord.forEach((plan, index) => {
        if (plan.dayOffset === null) {
          return null;
        }

        const offsetDate = classStart
          ? format(addDays(new Date(classStart), plan.dayOffset || 0), D_MMM_YYYY)
          : null;

        const label = `Days after start ${offsetDate ? `(${offsetDate})` : ""}`;

        expect(screen.getByLabelText(label).value).toBe(plan.dayOffset.toString());
      });
    }
  });
});
