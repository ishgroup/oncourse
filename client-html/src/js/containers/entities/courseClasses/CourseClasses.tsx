/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Dialog } from "@material-ui/core";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Typography from "@material-ui/core/Typography";
import React, { useCallback, useEffect, useState } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { getFormInitialValues, getFormValues, initialize } from "redux-form";

import { format } from "date-fns";
import {
  TableModel, CourseClass, Course, ClassCost, Account, ClassFundingSource, DeliveryMode, Enrolment, Outcome
} from "@api/model";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import Button from "../../../common/components/buttons/Button";
import { StyledCheckbox } from "../../../common/components/form/form-fields/CheckboxField";
import ListView from "../../../common/components/list-view/ListView";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { courseClassBudgetPath, courseClassTimetablePath, plainEnrolmentPath } from "../../../constants/Api";
import { FilterGroup } from "../../../model/common/ListView";
import {
  clearListState,
  getFilters,
  setListCreatingNew,
  setListEditRecord, setListSelection,
} from "../../../common/components/list-view/actions";
import EnrolmentService from "../enrolments/services/EnrolmentService";
import SendMessageEditView from "../messages/components/SendMessageEditView";
import OutcomeService from "../outcomes/services/OutcomeService";
import CourseClassCogWheel from "./components/CourseClassCogWheel";
import CourseClassEditView from "./components/CourseClassEditView";
import {
  createCourseClass, deleteCourseClass, getCourseClass, getCourseClassTags, updateCourseClass
} from "./actions";
import {
 AnyArgFunction, BooleanArgFunction, NoArgFunction, NumberArgFunction
} from "../../../model/common/CommonFunctions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { getTutorRoles } from "../../preferences/actions";
import { getPlainAccounts } from "../accounts/actions";
import { getPlainTaxes } from "../taxes/actions";
import { checkPermissions, getUserPreferences } from "../../../common/actions";
import { CourseClassExtended } from "../../../model/entities/CourseClass";
import { fieldUpdateHandler } from "../../../common/utils/actionsQueue";
import { validateTutorCreate, validateTutorUpdate } from "./components/tutors/utils";
import { postCourseClassTutor, putCourseClassTutor } from "./components/tutors/actions";
import { getActiveFundingContracts } from "../../avetmiss-export/actions";
import PreferencesService from "../../preferences/services/PreferencesService";
import { getVirtualSites } from "../sites/actions";
import { validateNoteCreate, validateNoteUpdate } from "../../../common/components/form/notes/utils";
import { postNoteItem, putNoteItem } from "../../../common/components/form/notes/actions";
import { State } from "../../../reducers/state";
import { normalizeNumberToZero } from "../../../common/utils/numbers/numbersNormalizing";
import { createCourseClassAssessment, updateCourseClassAssessment } from "./components/assessments/actions";
import { validateAssesmentCreate, validateAssesmentUpdate } from "./components/assessments/utils";
import EntityService from "../../../common/services/EntityService";
import history from "../../../constants/History";
import {
  DEFAULT_DELIVERY_MODE_KEY,
  DEFAULT_FUNDING_SOURCE_KEY,
  DEFAULT_MAXIMUM_PLACES_KEY,
  DEFAULT_MINIMUM_PLACES_KEY
} from "../../../constants/Config";
import { UserPreferencesState } from "../../../common/reducers/userPreferencesReducer";
import { III_DD_MMM_YYYY_HH_MM } from "../../../common/utils/dates/format";
import { appendTimezone } from "../../../common/utils/dates/formatTimezone";
import uniqid from "../../../common/utils/uniqid";

const manualLink = getManualLink("classes");

const nameCondition = (val: CourseClass) => val.courseName;

interface CourseClassesProps {
  onFirstRender?: NoArgFunction;
  onInit?: NoArgFunction;
  onUpdate?: (id: number, courseClass: CourseClass) => void;
  onCreate?: (courseClass: CourseClass) => void;
  onDelete?: NumberArgFunction;
  clearListState?: NoArgFunction;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  getCourseClass?: (id: string) => void;
  dispatch?: Dispatch;
  values?: CourseClass;
  initialValues?: CourseClass;
  userPreferences?: UserPreferencesState;
  setListCreatingNew?: BooleanArgFunction;
  updateSelection?: AnyArgFunction<string[]>;
}

export const classCostInitial: ClassCost = {
  id: null,
  courseClassid: null,
  taxId: null,
  accountId: null,
  invoiceId: null,
  description: null,
  invoiceToStudent: null,
  payableOnEnrolment: null,
  isSunk: false,
  maximumCost: null,
  minimumCost: null,
  onCostRate: null,
  perUnitAmountExTax: 0,
  perUnitAmountIncTax: 0,
  actualAmount: 0,
  unitCount: null,
  contactId: null,
  contactName: null,
  flowType: null,
  repetitionType: "Fixed",
  courseClassDiscount: null,
  paymentPlan: [],
  courseClassTutorId: null
};

const Initial: CourseClassExtended = {
  id: null,
  code: null,
  courseId: null,
  courseCode: null,
  courseName: null,
  endDateTime: null,
  startDateTime: null,
  attendanceType: "No information",
  deliveryMode: "Not Set",
  fundingSource: "Commonwealth and state - general recurrent",
  budgetedPlaces: 0,
  censusDate: null,
  createdOn: null,
  modifiedOn: null,
  deposit: null,
  detBookingId: null,
  expectedHours: null,
  feeExcludeGST: null,
  feeHelpClass: false,
  finalDetExport: null,
  initialDetExport: null,
  isActive: true,
  isCancelled: false,
  isDistantLearningCourse: false,
  isShownOnWeb: false,
  maxStudentAge: null,
  maximumDays: null,
  maximumPlaces: null,
  message: null,
  midwayDetExport: null,
  minStudentAge: null,
  minimumPlaces: null,
  reportableHours: 0,
  sessionsCount: null,
  suppressAvetmissExport: false,
  vetCourseSiteID: null,
  vetFundingSourceStateID: null,
  vetPurchasingContractID: null,
  vetPurchasingContractScheduleID: null,
  webDescription: null,
  relatedFundingSourceId: null,
  roomId: null,
  taxId: null,
  summaryFee: null,
  summaryDiscounts: null,
  enrolmentsToProfitLeftCount: null,
  successAndQueuedEnrolmentsCount: null,
  tags: [],
  documents: [],
  trainingPlan: [],
  sessions: [],
  tutors: [],
  budget: [],
  studentAttendance: [],
  tutorAttendance: [],
  notes: [],
  assessments: [],
  isTraineeship: false,
  customFields: {}
};

const filterGroups: FilterGroup[] = [
  {
    title: "TYPE",
    filters: [
      {
        name: "Classes",
        expression: "course.isTraineeship == false",
        active: false
      },
      {
        name: "Traineeships",
        expression: "course.isTraineeship == true",
        active: false
      }
    ]
  },
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Current classes",
        expression: "startDateTime < tomorrow and endDateTime >= today and isCancelled is false",
        active: true
      },
      {
        name: "Future classes",
        expression: "startDateTime >= tomorrow and endDateTime >= tomorrow and isCancelled is false",
        active: true
      },
      {
        name: "Self paced classes",
        expression: "isDistantLearningCourse is true and isCancelled is false",
        active: true
      },
      {
        name: "Unscheduled classes",
        expression:
          "(startDateTime is null or endDateTime is null) and isDistantLearningCourse is false and isCancelled is false",
        active: false
      },
      {
        name: "Finished classes",
        expression: "isCancelled is false and endDateTime before today",
        active: false
      },
      {
        name: "Cancelled classes",
        expression: "isCancelled is true",
        active: false
      }
    ]
  }
];

const findRelatedGroup: any[] = [
  { title: "All enrolments", list: "enrolment", expression: "courseClass.id" },
  { title: "Audits", list: "audit", expression: "entityIdentifier == CourseClass and entityId" },
  { title: "Courses", list: "course", expression: "courseClasses.id" },
  { title: "Discounts", list: "discount", expression: "discountCourseClasses.courseClass.id" },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == CourseClass and attachmentRelations.entityRecordId"
  },
  {
    title: "Enrolled students",
    list: "contact",
    expression: "student.enrolments.status == SUCCESS and student.enrolments.courseClass.id"
  },
  { title: "Invoices", list: "invoice", expression: "courseClasses.id" },
  { title: "Outcomes", list: "outcome", expression: "enrolment.courseClass.id" },
  { title: "Payslips", list: "payslip", expression: "paylines.classCost.courseClass.id" },
  { title: "Student feedback", list: "survey", expression: "enrolment.courseClass.id" },
  { title: "Tutors", list: "contact", expression: "tutor.courseClassRoles.courseClass.id" },
  {
    title: "Withdrawn students",
    list: "contact",
    expression: "student.enrolments.status in (CANCELLED, REFUNDED) and student.enrolments.courseClass.id"
  }
];

const preformatBeforeSubmit = (value: CourseClassExtended): Course => {
  const submitted = { ...value };

  delete submitted.tutors;
  delete submitted.sessions;
  delete submitted.budget;
  delete submitted.studentAttendance;
  delete submitted.tutorAttendance;
  delete submitted.notes;
  delete submitted.assessments;
  delete submitted.trainingPlan;
  delete submitted.openedSession;

  return submitted;
};

const asyncValidate = (values: CourseClassExtended, dispatch, props, blurredField) => {
  if (blurredField) {
    switch (true) {
      case blurredField.startsWith("notes"):
        return fieldUpdateHandler(
          values,
          dispatch,
          props,
          blurredField,
          "Note",
          "notes",
          validateNoteCreate,
          validateNoteUpdate,
          postNoteItem,
          putNoteItem
        );

      case blurredField.startsWith("assessments"):
        return fieldUpdateHandler(
          values,
          dispatch,
          props,
          blurredField,
          "AssessmentClass",
          "assessments",
          validateAssesmentCreate,
          validateAssesmentUpdate,
          createCourseClassAssessment,
          updateCourseClassAssessment
        );

      case blurredField.startsWith("tutors"):
        return fieldUpdateHandler(
          values,
          dispatch,
          props,
          blurredField,
          "CourseClassTutor",
          "tutors",
          validateTutorCreate,
          validateTutorUpdate,
          postCourseClassTutor,
          putCourseClassTutor,
          values.id ? null : uniqid()
        );
    }
  }
  return Promise.resolve();
};

const shouldAsyncValidate = ({ trigger, pristine, initialized }) => {
  switch (trigger) {
    case "blur":
    case "change":
      return true;
    case "submit":
      return !pristine || !initialized;
    default:
      return false;
  }
};

const setRowClasses = ({ isCancelled, isShownOnWeb, isActive }) => {
  if (isActive === "Yes" && isShownOnWeb === "Yes") return undefined;
  if (isActive === "Yes") return "op075";
  if (isActive === "No" || isCancelled === "Yes") return "op05";

  return undefined;
};

const formatSelfPaced = (v, row, columns) => {
  const timezoneIndex = columns
    .filter(c => c.visible === true || c.system === true)
    .findIndex(c => c.attribute === "clientTimeZoneId");
  const selfPacedIndex = columns
    .filter(c => c.visible === true || c.system === true)
    .findIndex(c => c.attribute === "isDistantLearningCourse");

  let timezone = null;
  let isSelfPaced = false;

  if (timezoneIndex !== -1) {
    timezone = row.values[timezoneIndex];
  }

  if (selfPacedIndex !== -1) {
    isSelfPaced = row.values[selfPacedIndex] === "true";
  }

  return isSelfPaced
    ? "Self paced"
    : v
      ? format(timezone ? appendTimezone(new Date(v), timezone) : new Date(v), III_DD_MMM_YYYY_HH_MM)
      : "";
};

const formatSelfPacedSessions = (v, row, columns) => {
  const selfPacedIndex = columns
    .filter(c => c.visible === true || c.system === true)
    .findIndex(c => c.attribute === "isDistantLearningCourse");

  let isSelfPaced = false;

  if (selfPacedIndex !== -1) {
    isSelfPaced = row.values[selfPacedIndex] === "true";
  }

  return isSelfPaced ? "" : v;
};

const customColumnFormats = {
  startDateTime: formatSelfPaced,
  endDateTime: formatSelfPaced,
  sessionsCount: formatSelfPacedSessions
};

const nestedEditFields = {
  SendMessage: props => <SendMessageEditView {...props} />
};

const defaultFields: Array<keyof CourseClass> = [
  "fundingSource",
  "deliveryMode",
  "relatedFundingSourceId",
  "vetFundingSourceStateID",
  "vetPurchasingContractID",
  "vetPurchasingContractScheduleID",
];

const outcomeUpdateFields: Array<keyof Outcome> = [
  "fundingSource",
  "vetFundingSourceStateID",
  "vetPurchasingContractID",
  "deliveryMode",
  "vetPurchasingContractScheduleID"
];

const enrolmentUpdateFields: Array<keyof Enrolment> = [
  "fundingSource",
  "relatedFundingSourceId",
  "vetFundingSourceStateID",
  "vetPurchasingContractID"
];

const getDefaultFieldName = (field: keyof CourseClass) => {
  switch (field) {
    case "fundingSource": {
      return "Default funding source national";
    }
    case "deliveryMode": {
      return "Default delivery mode";
    }
    case "relatedFundingSourceId": {
      return "Default funding contract";
    }
    case "vetFundingSourceStateID": {
      return "Default funding source - State";
    }
    case "vetPurchasingContractID": {
      return "Default purchasing contract identifier";
    }
    case "vetPurchasingContractScheduleID": {
      return "Default purchasing contract schedule identifier";
    }
    default:
      return "";
  }
};

const CourseClasses: React.FC<CourseClassesProps> = props => {
  const {
    onFirstRender,
    onDelete,
    onCreate,
    onUpdate,
    getCourseClass,
    userPreferences,
    setListCreatingNew,
    updateSelection,
    values,
    initialValues,
    dispatch
  } = props;

  const [populatedInitial, setPopulatedInitial] = useState<CourseClassExtended>({ ...Initial });
  const [changedFields, setChangedFields] = useState([]);

  useEffect(() => {
    onFirstRender();
  }, []);

  const onInit = useCallback((updatedInitial?: any) => {
    PreferencesService.getDeafaultIncomeAccount().then((account: Account) => {
      const studentFee: ClassCost = {
        taxId: account.tax.id,
        accountId: account.id,
        invoiceToStudent: true,
        payableOnEnrolment: true,
        isSunk: false,
        description: "Student enrolment fee",
        perUnitAmountExTax: 0,
        actualAmount: 0,
        flowType: "Income",
        repetitionType: "Per enrolment"
      };
      const custom: CourseClassExtended = {
        ...populatedInitial,
        ...updatedInitial || {},
        budget: [studentFee],
        minimumPlaces: normalizeNumberToZero(userPreferences[DEFAULT_MINIMUM_PLACES_KEY]),
        maximumPlaces: normalizeNumberToZero(userPreferences[DEFAULT_MAXIMUM_PLACES_KEY]),
        deliveryMode: DeliveryMode[userPreferences[DEFAULT_DELIVERY_MODE_KEY]],
        startDateTime: new Date().toISOString(),
        fundingSource: ClassFundingSource[userPreferences[DEFAULT_FUNDING_SOURCE_KEY]]
      };

      dispatch(setListEditRecord(custom));
      dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, custom));
      setPopulatedInitial({ ...Initial });
    });
  }, [userPreferences, populatedInitial]);

  useEffect(() => {
    if (window.location.search
      && userPreferences.hasOwnProperty(DEFAULT_MINIMUM_PLACES_KEY)
      && userPreferences.hasOwnProperty(DEFAULT_MAXIMUM_PLACES_KEY)
      && userPreferences.hasOwnProperty(DEFAULT_DELIVERY_MODE_KEY)
      && userPreferences.hasOwnProperty(DEFAULT_FUNDING_SOURCE_KEY)
    ) {
      const searchParam = new URLSearchParams(window.location.search);

      let courseId: any = searchParam.get("courseId");
      courseId = Number(courseId);

      if (courseId && !isNaN(courseId)) {
        EntityService.getPlainRecords(
          "Course",
          "name,code,nextAvailableCode,reportableHours",
          `id is ${courseId}`,
          1
        ).then(res => {
          if (res.rows.length > 0) {
            const updatedInitial = {
              ...populatedInitial,
              courseId,
              courseName: res.rows[0].values[0],
              courseCode: res.rows[0].values[1],
              code: res.rows[0].values[2],
              reportableHours: res.rows[0].values[3] ? Number(res.rows[0].values[3]) : 0
            };

            setListCreatingNew(true);
            updateSelection(["new"]);
            onInit(updatedInitial);

            const { pathname } = window.location;

            searchParam.delete("courseId");

            history.replace({
              pathname,
              search: decodeURIComponent(searchParam.toString())
            });
          }
        });
      }
    }
  }, [userPreferences]);

  const onBeforeSave = ({ onSaveArgs }) => {
    const changedValues = [];

    defaultFields.forEach(f => {
      if (initialValues[f] !== onSaveArgs[1][f]) {
        changedValues.push({
          name: f,
          label: getDefaultFieldName(f),
          value: onSaveArgs[1][f],
          updateForOutcome: false,
          updateForEnrolment: false,
        });
      }
    });

    if (changedValues.length) {
      setChangedFields(changedValues);
    } else {
      onUpdate(onSaveArgs[1].id, onSaveArgs[1]);
    }
  };

  const onSaveDefaultFieldChange = (value, name, field) => {
    const updated = [...changedFields];
    updated.find(f => f.name === name)[field] = value;
    setChangedFields(updated);
  };

  const onConfirm = () => {
    const outcomeFieldsToUpdate = changedFields.filter(f => f.updateForOutcome);
    const enrolmentFieldsToUpdate = changedFields.filter(f => f.updateForEnrolment);

    if (outcomeFieldsToUpdate.length) {
      EntityService.getPlainRecords("Outcome", "id", `enrolment.courseClass.id is ${values.id}`)
        .then(res => {
          const ids = res.rows.map(r => Number(r.id));
          return OutcomeService.bulkChange({
            ids,
            diff: outcomeFieldsToUpdate.reduce((p, o) => {
              p[o.name] = o.value;
              return p;
            }, {})
          });
        })
        .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to update related outcomes"));
    }

    if (enrolmentFieldsToUpdate.length) {
      EntityService.getPlainRecords("Enrolment", "id", `courseClass.id is ${values.id}`)
        .then(res => {
          const ids = res.rows.map(r => Number(r.id));

          return EnrolmentService.bulkChange({
            ids,
            diff: enrolmentFieldsToUpdate.reduce((p, o) => {
              p[o.name] = o.value;
              return p;
            }, {})
          });
        })
        .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to update related enrolments"));
    }

    setChangedFields([]);
    onUpdate(values.id, preformatBeforeSubmit(values));
  };

  return (
    <>
      <ListView
        listProps={{
          customColumnFormats,
          primaryColumn: "course.name",
          secondaryColumn: "uniqueCode",
          setRowClasses
        }}
        editViewProps={{
          manualLink,
          nameCondition,
          asyncValidate,
          shouldAsyncValidate,
          asyncBlurFields: [
            "tutors[].confirmedOn",
            "tutors[].roleId",
            "tutors[].contactId",
            "assessments[].assessmentCode",
            "assessments[].assessmentName",
            "assessments[].dueDate",
            "assessments[].releaseDate",
            "notes[].message"
          ],
          asyncChangeFields: [
            "tutors[].isInPublicity",
            "assessments[].submissions"
          ],
          hideFullScreenAppBar: true,
          enableReinitialize: true,
          keepDirtyOnReinitialize: true
        }}
        EditViewContent={CourseClassEditView}
        nestedEditFields={nestedEditFields}
        getEditRecord={getCourseClass}
        rootEntity="CourseClass"
        onInit={onInit}
        onDelete={onDelete}
        onCreate={onCreate}
        onSave={onUpdate}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        CogwheelAdornment={CourseClassCogWheel}
        onBeforeSave={onBeforeSave}
        preformatBeforeSubmit={preformatBeforeSubmit}
        alwaysFullScreenCreateView
      />

      <Dialog maxWidth="md" open={Boolean(changedFields.length)} disableBackdropClick disableEscapeKeyDown>
        <DialogTitle classes={{
          root: "pb-0"
        }}
        >
          You have updated the following default fields for the selected class:
        </DialogTitle>
        <DialogContent>
          <Typography className="mt-1" variant="caption" color="textSecondary">
            To update these same fields in any associated outcomes and enrolments, click the checkbox next to each field.
          </Typography>
          <Typography variant="caption" color="textSecondary" gutterBottom paragraph>
            If you do not want to update any fields, leave them unchecked.
          </Typography>
          <Typography variant="caption" color="textSecondary" paragraph>
            NOTE: This action will override any values previously set in these outcomes and enrolments.
          </Typography>
          <div className="d-flex">
            <div className="flex-fill">
              <div className="heading">
                Outcome
              </div>
              {
                changedFields.filter(f => outcomeUpdateFields.includes(f.name)).map(f => (
                  <div key={f.name + "updateForOutcome"}>
                    <FormControlLabel
                      control={(
                        <StyledCheckbox
                          checked={f.updateForOutcome}
                          onChange={(e, checked) => onSaveDefaultFieldChange(checked, f.name, "updateForOutcome")}
                        />
                      )}
                      label={f.label}
                    />
                  </div>
                ))
              }
            </div>
            <div className="flex-fill">
              <div className="heading">
                Enrolment
              </div>
              {
                changedFields.filter(f => enrolmentUpdateFields.includes(f.name)).map(f => (
                  <div key={f.name + "updateForEnrolment"}>
                    <FormControlLabel
                      key={f.name + "updateForEnrolment"}
                      control={(
                        <StyledCheckbox
                          checked={f.updateForEnrolment}
                          onChange={(e, checked) => onSaveDefaultFieldChange(checked, f.name, "updateForEnrolment")}
                        />
                      )}
                      label={f.label}
                    />
                  </div>
                ))
              }
            </div>
          </div>
        </DialogContent>
        <DialogActions className="pr-2 pb-2">
          <Button color="primary" onClick={onConfirm}>
            OK
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

const mapStateToProps = (state: State) => ({
  userPreferences: state.userPreferences,
  sessionSelection: state.courseClassesBulkSession.selection,
  values: getFormValues(LIST_EDIT_VIEW_FORM_NAME)(state),
  initialValues: getFormInitialValues(LIST_EDIT_VIEW_FORM_NAME)(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  onFirstRender: () => {
    dispatch(getFilters("CourseClass"));
    dispatch(getCourseClassTags());
    dispatch(
      getTutorRoles(
        null,
        null,
        "name,description,active,currentPayrate.oncostRate,currentPayrate.rate,currentPayrate.type"
      )
    );
    dispatch(checkPermissions({ path: "/a/v1/list/option/payroll?entity=CourseClass", method: "PUT" }));
    dispatch(
      checkPermissions({
        path: "/a/v1/list/option/payroll?entity=CourseClass&bulkConfirmTutorWages=true",
        method: "POST"
      })
    );
    dispatch(getFilters("CourseClass"));
    dispatch(getVirtualSites());
    dispatch(getPlainAccounts());
    dispatch(getPlainTaxes());
    dispatch(getActiveFundingContracts(true));
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
    dispatch(checkPermissions({ path: courseClassBudgetPath, method: "GET" }));
    dispatch(checkPermissions({ path: courseClassTimetablePath, method: "POST" }));
    dispatch(checkPermissions({ path: plainEnrolmentPath, method: "GET" }));
    dispatch(
      getUserPreferences([
        DEFAULT_DELIVERY_MODE_KEY,
        DEFAULT_FUNDING_SOURCE_KEY,
        DEFAULT_MAXIMUM_PLACES_KEY,
        DEFAULT_MINIMUM_PLACES_KEY
      ])
    );
  },
  getCourseClass: (id: string) => dispatch(getCourseClass(id)),
  onUpdate: (id: number, courseClass: CourseClass) => dispatch(updateCourseClass(id, courseClass)),
  onDelete: (id: number) => dispatch(deleteCourseClass(id)),
  onCreate: (courseClass: CourseClass) => dispatch(createCourseClass(courseClass)),
  clearListState: () => dispatch(clearListState()),
  setListCreatingNew: creatingNew => dispatch(setListCreatingNew(creatingNew)),
  updateSelection: selection => dispatch(setListSelection(selection))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CourseClasses);
