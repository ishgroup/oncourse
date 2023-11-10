/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CustomFieldType, Enrolment } from "@api/model";
import { Dialog, FormControlLabel } from "@mui/material";
import Button from "@mui/material/Button";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Typography from "@mui/material/Typography";
import { AnyArgFunction, openInternalLink, StyledCheckbox } from "ish-ui";
import React, { useCallback, useEffect, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getFormInitialValues, getFormValues, initialize } from "redux-form";
import { checkPermissions } from "../../../common/actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import { clearListState, getFilters, setListEditRecord, } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getWindowHeight, getWindowWidth } from "../../../common/utils/common";
import { getManualLink } from "../../../common/utils/getManualLink";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import { OutcomeChangeField } from "../../../model/entities/Enrolment";
import { State } from "../../../reducers/state";
import { getActiveFundingContracts } from "../../avetmiss-export/actions";
import { getGradingTypes } from "../../preferences/actions";
import { getListTags } from "../../tags/actions";
import { updateEntityRecord } from "../common/actions";
import { processOtcomeChangeFields, setOtcomeChangeFields } from "./actions";
import EnrolmentCogWheel from "./components/EnrolmentCogWheel";
import EnrolmentEditView from "./components/EnrolmentEditView";
import { getOutcomeCommonFieldName, outcomeCommonFields } from "./constants";

const nameCondition = (val: Enrolment) => val.studentName;

const manualLink = getManualLink("processingEnrolments");

interface EnrolmentsProps {
  onInit?: (initial: Enrolment) => void;
  onSave?: (id: number, enrolment: Enrolment) => void;
  getFilters?: () => void;
  clearListState?: () => void;
  getTags?: () => void;
  getGradingTypes?: () => void;
  getPermissions?: () => void;
  getFundingContracts?: () => void;
  customFieldTypesUpdating?: boolean;
  customFieldTypes?: CustomFieldType[];
  hasQePermissions?: boolean;
  initialValues?: Enrolment;
  values?: Enrolment;
  dispatch?: Dispatch;
  changedOutcomeFields?: OutcomeChangeField[],
  setChangedFields?: AnyArgFunction<OutcomeChangeField[]>
  processOtcomeChangeFields?: AnyArgFunction<number[]>
  selection?: number[];
}

const Initial: Enrolment = {
  associatedCourseIdentifier: null,
  attendanceType: "No information",
  courseClassId: 0,
  courseClassName: null,
  creditFOEId: null,
  creditLevel: "Advanced Diploma",
  creditOfferedValue: null,
  creditProvider: null,
  creditProviderType: "Not elsewhere categorised",
  creditTotal: "Unit of study consists wholly of RPL",
  creditType: "Other",
  creditUsedValue: null,
  cricosConfirmation: null,
  eligibilityExemptionIndicator: false,
  vetFeeExemptionType: "Not set",
  feeHelpAmount: 0,
  feeStatus: "Australian Capital Territory Government subsidised",
  vetIsFullTime: false,
  relatedFundingSourceId: 0,
  fundingSource: "Commonwealth - specific",
  vetFundingSourceStateID: null,
  id: 0,
  outcomeIdTrainingOrg: null,
  source: null,
  status: null,
  studentContactId: 0,
  studentName: null,
  studyReason: "Not stated",
  suppressAvetmissExport: false,
  tags: null,
  vetTrainingContractID: null,
  trainingPlanDeveloped: false,
  vetFeeIndicator: false,
  vetInSchools: false,
  vetPurchasingContractID: null,
  vetClientID: null,
  customFields: {}
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Current active",
        expression:
          "((courseClass.endDateTime == null) or (courseClass.endDateTime >= today)) and "
          + "((status == QUEUED) or (status == IN_TRANSACTION) or (status == SUCCESS))",
        active: true
      },
      {
        name: "Completed active",
        expression: "(courseClass.endDateTime != null) and (courseClass.endDateTime < today) and (status == SUCCESS)",
        active: false
      },
      {
        name: "Cancelled",
        expression: "status in (CANCELLED, REFUNDED)",
        active: false
      },
      {
        name: "Failed",
        expression: "status in (FAILED, FAILED_NO_PLACES, FAILED_CARD_DECLINED, CORRUPTED)",
        active: false
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Enrolment and entityId" },
  { title: "Classes", list: "class", expression: "enrolments.id" },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == Enrolment and attachmentRelations.entityRecordId"
  },
  { title: "Invoices", list: "invoice", expression: "invoiceLines.enrolment.id" },
  { title: "Outcomes", list: "outcome", expression: "enrolment.id" },
  { title: "VET reporting", list: "vetReporting", expression: "student.enrolments.id" },
  { title: "Voucher redeemed", list: "sale", expression: "redeemedEnrolment.id" },
  { title: "Students", list: "contact", expression: "student.enrolments.id" },
  { title: "Submissions", list: "assessmentSubmission", expression: "enrolment.id" },
  { title: "Certificates", list: "certificate", expression: "certificateOutcomes.outcome.enrolment.id" },

];

const Enrolments: React.FC<EnrolmentsProps> = props => {
  const {
    onInit,
    onSave,
    getFilters,
    getPermissions,
    clearListState,
    getTags,
    getFundingContracts,
    getGradingTypes,
    customFieldTypesUpdating,
    customFieldTypes,
    hasQePermissions,
    initialValues,
    values,
    changedOutcomeFields,
    setChangedFields,
    processOtcomeChangeFields,
    selection
  } = props;

  const [initNew, setInitNew] = useState(false);
  const [createMenuOpened, setCreateMenuOpened] = useState(false);

  useEffect(() => {
    getFilters();
    getTags();
    getFundingContracts();
    getPermissions();
    getGradingTypes();

    return clearListState;
  }, []);

  useEffect(() => {
    if (initNew && customFieldTypes && !customFieldTypesUpdating) {
      setInitNew(false);
      const customFields = {};
      onInit({ ...Initial, customFields });
    }
  }, [initNew, customFieldTypes, customFieldTypesUpdating]);

  const openCreateMenu = useCallback(() => {
    setCreateMenuOpened(true);
  }, []);

  const closeCreateMenu = useCallback(() => {
    setCreateMenuOpened(false);
  }, []);

  const onCreateClass = useCallback(() => {
    closeCreateMenu();
    openInternalLink("/checkout");
  }, []);

  const customOnCreate = () => {
    openCreateMenu();
  };

  const onBeforeSave = ({ onSaveArgs }) => {
    const changedValues = [];

    outcomeCommonFields.forEach(f => {
      if (initialValues[f] !== onSaveArgs[1][f]) {
        changedValues.push({
          name: f,
          label: getOutcomeCommonFieldName(f),
          value: onSaveArgs[1][f],
          update: false
        });
      }
    });

    if (changedValues.length) {
      setChangedFields(changedValues);
    } else {
      onSave(onSaveArgs[0], onSaveArgs[1]);
    }
  };

  const onSaveOutcomeFieldChange = (value, index) => {
    const updated = [...changedOutcomeFields];
    updated[index].update = value;
    setChangedFields(updated);
  };

  const onConfirm = () => {
    processOtcomeChangeFields(values?.id ? [values.id] : selection);

    if (values?.id) {
      onSave(values.id, values);
    }

    setChangedFields([]);
  };

  return (
    <>
      <ListView
        listProps={{
          primaryColumn: "student.contact.fullName",
          secondaryColumn: "courseClass.course.name"
        }}
        editViewProps={{
          manualLink,
          nameCondition,
          asyncValidate: notesAsyncValidate,
          asyncChangeFields: ["notes[].message"],
          hideTitle: true
        }}
        EditViewContent={EnrolmentEditView}
        rootEntity="Enrolment"
        onInit={() => setInitNew(true)}
        customOnCreate={customOnCreate}
        onBeforeSave={onBeforeSave}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        CogwheelAdornment={EnrolmentCogWheel}
        defaultDeleteDisabled
        alwaysFullScreenCreateView
      />
      <Menu
        id="createMenu"
        open={createMenuOpened}
        onClose={closeCreateMenu}
        disableAutoFocusItem
        anchorReference="anchorPosition"
        anchorPosition={{ top: getWindowHeight() - 80, left: getWindowWidth() - 200 }}
        anchorOrigin={{
          vertical: "center",
          horizontal: "center"
        }}
        transformOrigin={{
          vertical: "center",
          horizontal: "center"
        }}
      >
        <MenuItem
          onClick={onCreateClass}
          disabled={!hasQePermissions}
          classes={{
            root: "listItemPadding"
          }}
        >
          Create Enrolment
        </MenuItem>
      </Menu>

      <Dialog open={Boolean(changedOutcomeFields.length)} disableEscapeKeyDown>
        <DialogTitle classes={{
          root: "pb-0"
        }}
        >
          You have updated the following enrolment default fields:
        </DialogTitle>
        <DialogContent className="pb-0">
          <div className="mt-2 mb-2">
            {
              changedOutcomeFields.map((f, i) => (
                <FormControlLabel
                  key={f.name}
                  className="checkbox"
                  control={(
                    <StyledCheckbox
                      checked={changedOutcomeFields[i].update}
                      onChange={(e, checked) => onSaveOutcomeFieldChange(checked, i)}
                    />
                  )}
                  label={changedOutcomeFields[i].label}
                />
              ))
            }
          </div>
          <Typography variant="caption" color="textSecondary">
            To update these same fields in any associated outcomes, click the checkbox next to each field.
          </Typography>
          <Typography variant="caption" color="textSecondary" gutterBottom paragraph>
            If you do not want to update any fields, leave them unchecked.
          </Typography>

          <Typography variant="caption" color="textSecondary" paragraph>
            NOTE: This action will override any values previously set in these outcomes.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button color="primary" onClick={onConfirm}>
            OK
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

const mapStateToProps = (state: State) => ({
  initialValues: getFormInitialValues(LIST_EDIT_VIEW_FORM_NAME)(state),
  values: getFormValues(LIST_EDIT_VIEW_FORM_NAME)(state),
  customFieldTypesUpdating: state.customFieldTypes.updating,
  customFieldTypes: state.customFieldTypes.types["Enrolment"],
  hasQePermissions: state.access.ENROLMENT_CREATE,
  changedOutcomeFields: state.enrolments.changedOutcomeFields,
  selection: state.list.selection
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  onInit: (initial: Enrolment) => {
    dispatch(setListEditRecord(initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, initial));
  },
  getTags: () => dispatch(getListTags("Enrolment")),
  getGradingTypes: () => dispatch(getGradingTypes()),
  getFilters: () => dispatch(getFilters("Enrolment")),
  getFundingContracts: () => dispatch(getActiveFundingContracts(true)),
  clearListState: () => dispatch(clearListState()),
  onSave: (id: number, enrolment: Enrolment) => dispatch(updateEntityRecord(id, "Enrolment", enrolment)),
  getPermissions: () => {
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
  },
  setChangedFields: fields => dispatch(setOtcomeChangeFields(fields)),
  processOtcomeChangeFields: ids => dispatch(processOtcomeChangeFields(ids))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Enrolments);