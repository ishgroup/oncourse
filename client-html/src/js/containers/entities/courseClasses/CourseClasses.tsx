/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import {
  Account,
  ClassCost,
  ClassFundingSource,
  CourseClass,
  CourseClassType,
  DeliveryMode,
  Enrolment,
  Outcome,
  TableModel
} from '@api/model';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControlLabel, Typography } from '@mui/material';
import $t from '@t';
import { format } from 'date-fns';
import {
  appendTimezone,
  BooleanArgFunction,
  III_DD_MMM_YYYY_HH_MM,
  NoArgFunction,
  normalizeNumberToZero,
  StyledCheckbox
} from 'ish-ui';
import React, { useCallback, useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { FormErrors, getFormInitialValues, getFormValues, initialize } from 'redux-form';
import { checkPermissions, getUserPreferences } from '../../../common/actions';
import { getCommonPlainRecords } from '../../../common/actions/CommonPlainRecordsActions';
import { IAction } from '../../../common/actions/IshAction';
import instantFetchErrorHandler from '../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import { postNoteItem, putNoteItem } from '../../../common/components/form/notes/actions';
import { validateNoteCreate, validateNoteUpdate } from '../../../common/components/form/notes/utils';
import {
  getFilters,
  setListEditRecord,
  setListSelection,
} from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { UserPreferencesState } from '../../../common/reducers/userPreferencesReducer';
import EntityService from '../../../common/services/EntityService';
import { fieldUpdateHandler } from '../../../common/utils/actionsQueue';
import { getManualLink } from '../../../common/utils/getManualLink';
import uniqid from '../../../common/utils/uniqid';
import { courseClassCancelPath, courseClassTimetablePath } from '../../../constants/Api';
import {
  DEFAULT_DELIVERY_MODE_KEY,
  DEFAULT_FUNDING_SOURCE_KEY,
  DEFAULT_MAXIMUM_PLACES_KEY,
  DEFAULT_MINIMUM_PLACES_KEY,
  PLAIN_LIST_MAX_PAGE_SIZE
} from '../../../constants/Config';
import history from '../../../constants/History';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { CourseClassExtended } from '../../../model/entities/CourseClass';
import { State } from '../../../reducers/state';
import { getActiveFundingContracts } from '../../avetmiss-export/actions';
import { getGradingTypes, getTutorRoles } from '../../preferences/actions';
import PreferencesService from '../../preferences/services/PreferencesService';
import { getEntitySpecialTags } from '../../tags/actions';
import { getPlainAccounts } from '../accounts/actions';
import { getVirtualSites } from '../sites/actions';
import { getPlainTaxes } from '../taxes/actions';
import { getCourseClassTags, updateCourseClass } from './actions';
import { createCourseClassAssessment, updateCourseClassAssessment } from './components/assessments/actions';
import { validateAssesmentCreate, validateAssesmentUpdate } from './components/assessments/utils';
import CourseClassCogWheel from './components/CourseClassCogWheel';
import CourseClassEditView from './components/CourseClassEditView';
import { postCourseClassTutor, putCourseClassTutor } from './components/tutors/actions';
import { validateTutorCreate, validateTutorUpdate } from './components/tutors/utils';

const manualLink = getManualLink("classes");

const nameCondition = (val: CourseClass) => val.courseName;

interface CourseClassesProps {
  onFirstRender?: NoArgFunction;
  onInit?: NoArgFunction;
  onUpdate?: (id: number, courseClass: CourseClass) => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  dispatch?: Dispatch<IAction>;
  values?: CourseClass;
  initialValues?: CourseClass;
  userPreferences?: UserPreferencesState;
  updateSelection?: (selection: string[]) => void;
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
  type: "With Sessions",
  isShownOnWeb: false,
  maxStudentAge: null,
  maximumDays: null,
  maximumPlaces: null,
  message: null,
  midwayDetExport: null,
  minStudentAge: null,
  minimumPlaces: null,
  reportableHours: 0,
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
        name: "Self-paced classes",
        expression: "type is DISTANT_LEARNING and isCancelled is false and (endDateTime is null or endDateTime >= today)",
        active: true
      },
      {
        name: "Hybrid classes",
        expression: "type is HYBRID and isCancelled is false",
        active: true
      },
      {
        name: "Unscheduled classes",
        expression:
          "(startDateTime is null or endDateTime is null) and type is WITH_SESSIONS and isCancelled is false",
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

const findRelatedGroup: FindRelatedItem[] = [
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
  { title: "Submissions", list: "assessmentSubmission", expression: "assessmentClass.courseClass.id" },
  { title: "Timetable", list: "timetable", expression: "courseClass.id" },
  { title: "Tutors", list: "contact", expression: "tutor.courseClassRoles.courseClass.id" },
  {
    title: "VET reporting",
    list: "vetReporting",
    expression: "student.enrolments.courseClass.id"
  },
  {
    title: "Withdrawn students",
    list: "contact",
    expression: "student.enrolments.status in (CANCELLED, REFUNDED) and student.enrolments.courseClass.id"
  }
];

const preformatBeforeSubmit = (value: CourseClassExtended): CourseClass => {
  const submitted: CourseClassExtended = { ...value };

  delete submitted.tutors;
  delete submitted.sessions;
  delete submitted.budget;
  delete submitted.studentAttendance;
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

const setRowClasses = ({ isCancelled, isShownOnWeb, isActive }) => {
  if (isActive === "Yes" && isShownOnWeb === "Yes") return undefined;
  if (isActive === "Yes") return "text-op065";
  if (isActive === "No" || isCancelled === "Yes") return "text-op05";

  return undefined;
};

const formatSelfPaced = (v, row, columns) => {
  const timezoneIndex = columns
    .filter(c => c.visible === true || c.system === true)
    .findIndex(c => c.attribute === "clientTimeZoneId");
  const selfPacedIndex = columns
    .filter(c => c.visible === true || c.system === true)
    .findIndex(c => c.attribute === "type");

  let timezone = null;

  if (timezoneIndex !== -1) {
    timezone = row.values[timezoneIndex];
  }

  const dateValue =  v
    ? format(timezone ? appendTimezone(new Date(v), timezone) : new Date(v), III_DD_MMM_YYYY_HH_MM)
    : "";

  if (selfPacedIndex !== -1) {
    const type: CourseClassType = row.values[selfPacedIndex];

    if (type === "Distant Learning" ) return $t("Self-paced");
    if (type === "Hybrid") return  dateValue || $t("Hybrid");
  }

  return dateValue;
};

const formatSelfPacedSessions = (v, row, columns) => {
  const selfPacedIndex = columns
    .filter(c => c.visible === true || c.system === true)
    .findIndex(c => c.attribute === "type");

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
  "vetPurchasingContractID",
  "vetPurchasingContractScheduleID",
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

const validate = (values: CourseClassExtended) => {
  const errors: FormErrors<CourseClassExtended> = {};

  if (values.type === 'Hybrid' && !values.sessions.length) {
    errors.sessions = "At least one timetable session must exist";
  }

  return errors;
};

const CourseClasses: React.FC<CourseClassesProps> = props => {
  const {
    onFirstRender,
    onUpdate,
    userPreferences,
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
        deliveryMode: DeliveryMode[userPreferences[DEFAULT_DELIVERY_MODE_KEY]] || Initial.deliveryMode,
        startDateTime: new Date().toISOString(),
        fundingSource: ClassFundingSource[userPreferences[DEFAULT_FUNDING_SOURCE_KEY]] || Initial.fundingSource
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

    if (values) {
      if (outcomeFieldsToUpdate.length) {
        EntityService.getPlainRecords("Outcome", "id", `enrolment.courseClass.id is ${values?.id}`)
          .then(res => {
            const ids = res.rows.map(r => Number(r.id));
            return EntityService.bulkChange('Outcome', {
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
        EntityService.getPlainRecords("Enrolment", "id", `courseClass.id is ${values?.id}`)
          .then(res => {
            const ids = res.rows.map(r => Number(r.id));

            return EntityService.bulkChange('Enrolment', {
              ids,
              diff: enrolmentFieldsToUpdate.reduce((p, o) => {
                p[o.name] = o.value;
                return p;
              }, {})
            });
          })
          .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to update related enrolments"));
      }
    }

    setChangedFields([]);
    if (values) onUpdate(values.id, preformatBeforeSubmit(values));
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
          validate,
          nameCondition,
          asyncValidate,
          asyncBlurFields: [
            "tutors[].confirmedOn",
            "tutors[].roleId",
            "tutors[].contactId",
            "assessments[].assessmentCode",
            "assessments[].assessmentName",
            "assessments[].dueDate",
            "assessments[].releaseDate"
          ],
          asyncChangeFields: [
            "tutors[].isInPublicity",
            "assessments[].contactIds",
            "assessments[].submissions",
            "notes[].message"
          ],
          hideTitle: true,
          enableReinitialize: true,
          keepDirtyOnReinitialize: true
        }}
        EditViewContent={CourseClassEditView}
        rootEntity="CourseClass"
        onInit={onInit}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        CogwheelAdornment={CourseClassCogWheel}
        onBeforeSave={onBeforeSave}
        preformatBeforeSubmit={preformatBeforeSubmit}
        alwaysFullScreenCreateView
      />

      <Dialog maxWidth="md" open={Boolean(changedFields.length)} disableEscapeKeyDown>
        <DialogTitle classes={{
          root: "pb-0"
        }}
        >
          {$t('you_have_updated_the_following_default_fields_for')}
        </DialogTitle>
        <DialogContent>
          <Typography className="mt-1" variant="caption" color="textSecondary">
            {$t('to_update_these_same_fields_in_any_associated_outc')}
          </Typography>
          <Typography variant="caption" color="textSecondary" gutterBottom paragraph>
            {$t('if_you_do_not_want_to_update_any_fields_leave_them')}
          </Typography>
          <Typography variant="caption" color="textSecondary" paragraph>
            {$t('note_this_action_will_override_any_values_previous')}
          </Typography>
          <div className="d-flex">
            <div className="flex-fill">
              <div className="heading">
                {$t('outcome')}
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
                {$t('enrolment')}
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
            {$t('ok')}
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

const mapDispatchToProps = (dispatch: Dispatch<IAction>) => ({
  dispatch,
  onFirstRender: () => {
    dispatch(getEntitySpecialTags('CourseClass'));
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
    getPlainAccounts(dispatch);
    dispatch(getPlainTaxes());
    dispatch(getGradingTypes());
    dispatch(getActiveFundingContracts(true));
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
    dispatch(checkPermissions({ path: courseClassTimetablePath, method: "POST" }));
    dispatch(checkPermissions({ path: courseClassCancelPath, method: "POST" }));
    dispatch(
      getUserPreferences([
        DEFAULT_DELIVERY_MODE_KEY,
        DEFAULT_FUNDING_SOURCE_KEY,
        DEFAULT_MAXIMUM_PLACES_KEY,
        DEFAULT_MINIMUM_PLACES_KEY
      ])
    );
    dispatch(getCommonPlainRecords("Site", 0, "name,localTimezone,isVirtual", true, "name", PLAIN_LIST_MAX_PAGE_SIZE));
  },
  onUpdate: (id: number, courseClass: CourseClass) => dispatch(updateCourseClass(id, courseClass)),
  updateSelection: selection => dispatch(setListSelection(selection)),
});

export default connect(mapStateToProps, mapDispatchToProps)(CourseClasses);