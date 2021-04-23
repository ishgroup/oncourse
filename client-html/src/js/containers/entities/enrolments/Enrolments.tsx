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
import React, {
   useCallback, useEffect, useState
} from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
 getFormInitialValues, getFormValues, initialize
} from "redux-form";
import MenuItem from "@material-ui/core/MenuItem";
import Menu from "@material-ui/core/Menu";
import { Enrolment, CustomFieldType } from "@api/model";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import Button from "../../../common/components/buttons/Button";
import { StyledCheckbox } from "../../../common/components/form/form-fields/CheckboxField";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import {
  setListEditRecord,
  getFilters,
  clearListState,
} from "../../../common/components/list-view/actions";
import EntityService from "../../../common/services/EntityService";
import { getWindowHeight, getWindowWidth, stubFunction } from "../../../common/utils/common";
import { defaultContactName } from "../contacts/utils";
import SendMessageEditView from "../messages/components/SendMessageEditView";
import OutcomeService from "../outcomes/services/OutcomeService";
import { getEnrolment, updateEnrolment } from "./actions";
import ListView from "../../../common/components/list-view/ListView";
import { FilterGroup } from "../../../model/common/ListView";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { getManualLink } from "../../../common/utils/getManualLink";
import { getListTags } from "../../tags/actions";
import ContactEditView from "../contacts/components/ContactEditView";
import EnrolmentCogWheel from "./components/EnrolmentCogWheel";
import EnrolmentEditView from "./components/EnrolmentEditView";
import { getActiveFundingContracts } from "../../avetmiss-export/actions";
import { getCustomFieldTypes } from "../customFieldTypes/actions";
import { State } from "../../../reducers/state";
import { openInternalLink } from "../../../common/utils/links";
import { checkPermissions } from "../../../common/actions";
import { Classes } from "../../../model/entities/CourseClass";
import { getGradingTypes } from "../../preferences/actions";

const nameCondition = (val: Enrolment) => defaultContactName(val.studentName);

const manualLink = getManualLink("processingEnrolments");

interface EnrolmentsProps {
  getEnrolmentRecord?: () => void;
  onInit?: (initial: Enrolment) => void;
  onSave?: (id: number, enrolment: Enrolment) => void;
  getFilters?: () => void;
  clearListState?: () => void;
  getTags?: () => void;
  getGradingTypes?: () => void;
  getPermissions?: () => void;
  getFundingContracts?: () => void;
  getCustomFieldTypes?: () => void;
  customFieldTypesUpdating?: boolean;
  customFieldTypes?: CustomFieldType[];
  hasQePermissions?: boolean;
  initialValues?: Enrolment;
  values?: Enrolment;
  dispatch?: Dispatch;
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
  feeCharged: 0,
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

const findRelatedGroup: any = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Enrolment and entityId" },
  { title: "Classes", list: Classes.path, expression: "enrolments.id" },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == Enrolment and attachmentRelations.entityRecordId"
  },
  { title: "Invoices", list: "invoice", expression: "invoiceLines.enrolment.id" },
  { title: "Outcomes", list: "outcome", expression: "enrolment.id" },
  { title: "Voucher redeemed", list: "sale", expression: "redeemedEnrolment.id" },
  { title: "Students", list: "contact", expression: "student.enrolments.id" },
  { title: "Submissions", list: "assessmentSubmission", expression: "enrolment.id" },
  { title: "Certificates", list: "certificate", expression: "certificateOutcomes.outcome.enrolment.id" },

];

const nestedEditFields = {
  Contact: props => <ContactEditView {...props} />,
  SendMessage: props => <SendMessageEditView {...props} />
};

const defaultFields: Array<keyof Enrolment> = ["fundingSource", "vetFundingSourceStateID", "vetPurchasingContractID"];

const getDefaultFieldName = (field: keyof Enrolment) => {
  switch (field) {
    case "fundingSource": {
      return "Default funding source national";
    }
    case "vetFundingSourceStateID": {
      return "Default funding source - State";
    }
    case "vetPurchasingContractID": {
      return "Default purchasing contract identifier";
    }
    default:
      return "";
  }
};

const Enrolments: React.FC<EnrolmentsProps> = props => {
  const {
    getEnrolmentRecord,
    onInit,
    onSave,
    getFilters,
    getPermissions,
    clearListState,
    getTags,
    getFundingContracts,
    getCustomFieldTypes,
    getGradingTypes,
    customFieldTypesUpdating,
    customFieldTypes,
    hasQePermissions,
    initialValues,
    values,
    dispatch
  } = props;

  const [initNew, setInitNew] = useState(false);
  const [createMenuOpened, setCreateMenuOpened] = useState(false);
  const [changedFields, setChangedFields] = useState([]);

  useEffect(() => {
    getCustomFieldTypes();
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
      // customFieldTypes.forEach((field: CustomFieldType) => {
      //   if (field.mandatory) {
      //     customFields[field.fieldKey] = null;
      //   }
      //   if (field.defaultValue && !field.defaultValue.match(/[;*]/g)) {
      //     customFields[field.fieldKey] = field.defaultValue;
      //   }
      // });
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

    defaultFields.forEach(f => {
      if (initialValues[f] !== onSaveArgs[1][f]) {
        changedValues.push({
          name: f,
          label: getDefaultFieldName(f),
          value: onSaveArgs[1][f],
          update: false
        });
      }
    });

    if (changedValues.length) {
      setChangedFields(changedValues);
    } else {
      onSave(onSaveArgs[1].id, onSaveArgs[1]);
    }
  };

  const onSaveOutcomeFieldChange = (value, index) => {
    const updated = [...changedFields];
    updated[index].update = value;
    setChangedFields(updated);
  };

  const onConfirm = () => {
    const outcomeFieldsToUpdate = changedFields.filter(f => f.update);

    if (outcomeFieldsToUpdate.length) {
      EntityService.getPlainRecords("Outcome", "id", `enrolment.id is ${values.id}`)
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

    setChangedFields([]);
    onSave(values.id, values);
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
          asyncBlurFields: ["notes[].message"]
        }}
        EditViewContent={EnrolmentEditView}
        getEditRecord={getEnrolmentRecord}
        rootEntity="Enrolment"
        nestedEditFields={nestedEditFields}
        onInit={() => setInitNew(true)}
        customOnCreate={customOnCreate}
        onBeforeSave={onBeforeSave}
        onSave={onSave}
        onCreate={stubFunction}
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

      <Dialog open={Boolean(changedFields.length)} disableBackdropClick disableEscapeKeyDown>
        <DialogTitle classes={{
          root: "pb-0"
        }}
        >
          You have updated the following default fields for the selected enrolment:
        </DialogTitle>
        <DialogContent>
          {
            changedFields.map((f, i) => (
              <div>
                <FormControlLabel
                  key={f.name}
                  className="checkbox pb-3 pt-3"
                  control={(
                    <StyledCheckbox
                      checked={changedFields[i].update}
                      onChange={(e, checked) => onSaveOutcomeFieldChange(checked, i)}
                    />
                  )}
                  label={changedFields[i].label}
                />
              </div>
            ))
          }
          <Typography className="mt-1" variant="caption" color="textSecondary">
            To update these same fields in any associated outcomes, click the checkbox next to each field.
          </Typography>
          <Typography variant="caption" color="textSecondary" gutterBottom paragraph>
            If you do not want to update any fields, leave them unchecked.
          </Typography>

          <Typography variant="caption" color="textSecondary" paragraph>
            NOTE: This action will override any values previously set in these outcomes.
          </Typography>
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
  initialValues: getFormInitialValues(LIST_EDIT_VIEW_FORM_NAME)(state),
  values: getFormValues(LIST_EDIT_VIEW_FORM_NAME)(state),
  customFieldTypesUpdating: state.customFieldTypes.updating,
  customFieldTypes: state.customFieldTypes.types["Enrolment"],
  hasQePermissions: state.access.ENROLMENT_CREATE
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
  getCustomFieldTypes: () => dispatch(getCustomFieldTypes("Enrolment")),
  clearListState: () => dispatch(clearListState()),
  getEnrolmentRecord: (id: string) => dispatch(getEnrolment(id)),
  onSave: (id: number, enrolment: Enrolment) => dispatch(updateEnrolment(id, enrolment)),
  getPermissions: () => {
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
  }
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Enrolments);
