/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  ClassFundingSource, CourseEnrolmentType, CourseStatus, DeliveryMode, OutcomeStatus
} from "@api/model";
import { EntityName } from "../../../../../../containers/automation/constants";
import { SelectItemDefault } from "../../../../../../model/entities/common";
import { mapSelectItems } from "../../../../../utils/common";
import {
  validateFundingSourse,
  validatePurchasingContractScheduleIdentifier,
  validateVetPurchasingContractIdentifier
} from "../../../../../utils/validation";

export interface BulkEditField {
  keyCode: string;
  label: string;
  name: string;
  type: string;
  items?: SelectItemDefault[];
  propsItemKey?: string;
  selectValueMark?: string;
  selectLabelMark?: string;
  defaultValue?: any;
  validate?: any;
  componentProps?: any
}

export const getBulkEditFields = (entity: EntityName): BulkEditField[] => {
  switch (entity) {
    case "Outcome": {
      return [
        {
          keyCode: "status",
          label: "Status",
          name: "Status",
          type: "Select",
          items: Object.keys(OutcomeStatus).map(mapSelectItems),
          defaultValue: "Not set"
        },
        {
          keyCode: "fundingSource",
          label: "Funding source",
          name: "Funding source",
          type: "Select",
          items: Object.keys(ClassFundingSource).map(mapSelectItems),
          defaultValue: "Not set"
        },
        {
          keyCode: "vetPurchasingContractID",
          label: "Purchasing contract identifier",
          name: "Purchasing contract identifier",
          type: "Text",
          validate: validateVetPurchasingContractIdentifier
        },
        {
          keyCode: "vetFundingSourceStateID",
          label: "Funding source state",
          name: "Funding source state",
          type: "Text",
          validate: validateFundingSourse
        },
        {
          keyCode: "vetPurchasingContractScheduleID",
          label: "Purchasing contract schedule identifier",
          name: "Purchasing contract schedule identifier",
          type: "Text",
          validate: validatePurchasingContractScheduleIdentifier
        },
        {
          keyCode: "deliveryMode",
          label: "Delivery mode",
          name: "Delivery mode",
          type: "Select",
          items: Object.keys(DeliveryMode).map(mapSelectItems),
          defaultValue: "Not set"
        },
        {
          keyCode: "reportableHours",
          label: "Reportable hours",
          name: "Reportable hours",
          type: "Number"
        }
      ];
    }
    case "CourseClass": {
      return [
        {
          keyCode: "isActive",
          label: "Enrolments allowed",
          name: "Enrolments allowed",
          type: "Switch",
          defaultValue: true
        },
        {
          keyCode: "isShownOnWeb",
          label: "Show on web",
          name: "Show on web",
          type: "Switch",
          defaultValue: true
        },
        {
          keyCode: "bulkTag",
          label: "Add tags",
          name: "bulkTag",
          type: "Tag",
          defaultValue: []
        },
        {
          keyCode: "bulkUntag",
          label: "Remove tags",
          name: "bulkUntag",
          type: "Tag",
          defaultValue: []
        }
      ];
    }
    case "Contact": {
      return [
        {
          keyCode: "bulkTag",
          label: "Add tags",
          name: "bulkTag",
          type: "Tag",
          defaultValue: []
        },
        {
          keyCode: "bulkUntag",
          label: "Remove tags",
          name: "bulkUntag",
          type: "Tag",
          defaultValue: []
        }
      ];
    }
    case "Course": {
      return [
        {
          keyCode: "enrolmentType",
          label: "Enrolment type",
          name: "Enrolment type",
          type: "Select",
          items: Object.keys(CourseEnrolmentType).map(mapSelectItems)
        },
        {
          keyCode: "status",
          label: "Status",
          name: "Status",
          type: "Select",
          items: Object.keys(CourseStatus).map(mapSelectItems)
        },
        {
          keyCode: "dataCollectionRuleId",
          label: "Data collection rule",
          name: "Data collection rule",
          type: "Select",
          selectValueMark: "id",
          selectLabelMark: "name",
          propsItemKey: "dataCollectionRules"
        },
        {
          keyCode: "bulkTag",
          label: "Add tags",
          name: "bulkTag",
          type: "Tag",
          defaultValue: []
        },
        {
          keyCode: "bulkUntag",
          label: "Remove tags",
          name: "bulkUntag",
          type: "Tag",
          defaultValue: []
        }
      ];
    }
    case "Enrolment": {
      return [
        {
          keyCode: "fundingSource",
          label: "Default funding source national",
          name: "Default funding source national",
          type: "Select",
          items: Object.keys(ClassFundingSource).map(mapSelectItems),
          defaultValue: "Not set"
        },
        {
          keyCode: "relatedFundingSourceId",
          label: "Funding contract",
          name: "Funding contract",
          type: "Select",
          propsItemKey: "contracts",
          selectValueMark: "id",
          selectLabelMark: "name",
          defaultValue: -1
        },
        {
          keyCode: "vetFundingSourceStateID",
          label: "Default funding source - State",
          name: "Default funding source - State",
          type: "Text"
        },
        {
          keyCode: "vetPurchasingContractID",
          label: "Default purchasing contract identifier (NSW Commitment ID)",
          name: "Default purchasing contract identifier (NSW Commitment ID)",
          type: "Text"
        },
        {
          keyCode: "bulkTag",
          label: "Add tags",
          name: "bulkTag",
          type: "Tag",
          defaultValue: []
        },
        {
          keyCode: "bulkUntag",
          label: "Remove tags",
          name: "bulkUntag",
          type: "Tag",
          defaultValue: []
        }
      ];
    }
    case "AssessmentSubmission": {
      return [
        {
          keyCode: "submittedOn",
          label: "Submitted On",
          name: "Submitted On",
          type: "Date"
        },
        {
          keyCode: "markedOn",
          label: "Marked On",
          name: "Marked On",
          type: "Date"
        }
      ];
    }
    default:
      // eslint-disable-next-line no-console
      console.warn(`There is no bulk edit fields for ${entity}`);
      return null;
  }
};
