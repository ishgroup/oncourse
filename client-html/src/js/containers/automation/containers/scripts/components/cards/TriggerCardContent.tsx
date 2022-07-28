/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo, useState } from "react";
import IconButton from "@mui/material/IconButton";
import Help from "@mui/icons-material/Help";
import * as Entities from "@aql/queryLanguageModel";
import { change } from "redux-form";
import { Script, TagRequirementType, TriggerType } from "@api/model";
import { Dispatch } from "redux";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import { AQL_ENTITY_ITEMS } from "../../../../constants";
import { mapSelectItems } from "../../../../../../common/utils/common";
import { SelectItemDefault } from "../../../../../../model/entities/common";
import { CatalogItemType } from "../../../../../../model/common/Catalog";
import TagsService from "../../../../../tags/services/TagsService";
import instantFetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";

const AllEntities = [...AQL_ENTITY_ITEMS, { label: "ContactTagRelation", value: "ContactTagRelation" }];

const TagableEntities = Object.keys(TagRequirementType).map(mapSelectItems);

interface Props {
  TriggerTypeItems: SelectItemDefault[];
  ScheduleTypeItems: SelectItemDefault[];
  enableEntityNameField: boolean;
  values: Script;
  isInternal: boolean;
  dispatch: Dispatch;
  form: string;
  timeZone: string;
  classes: any;
  checklists: CatalogItemType[];
}

const ChecklistsTriggers: TriggerType[] = [
  'Checklist task checked',
  'Checklist completed'
];

const TriggerCardContent = (props: Props) => {
  const {
    TriggerTypeItems, ScheduleTypeItems, enableEntityNameField, values, isInternal, dispatch, form, timeZone, checklists
  } = props;
  
  const [entityItems, setEntityItems] = useState(AllEntities);

  const isScheduleOrOnDemand = useMemo(() => Boolean(
    (values.trigger && values.trigger.type === "On demand") || (values.trigger && values.trigger.type === "Schedule")
  ), [values.trigger.type]);

  const onTriggerChange = (e, newType: TriggerType) => {
    if (
      (ChecklistsTriggers.includes(values.trigger.type) && !ChecklistsTriggers.includes(newType))
      || (!ChecklistsTriggers.includes(values.trigger.type) && ChecklistsTriggers.includes(newType))
    ) {
      dispatch(change(form, "trigger.entityName", null));
    }
    dispatch(change(form, "trigger.parameterId", null));
  };

  useEffect(() => {
    if (values.trigger.entityAttribute) {
      dispatch(change(form, "trigger.entityAttribute", null));
    }
  }, [values.trigger.type, values.trigger.entityName]);

  useEffect(() => {
    setEntityItems(ChecklistsTriggers.includes(values?.trigger?.type)
      ? TagableEntities
      : AllEntities);
  }, [values.trigger.type]);

  useEffect(() => {
    if (typeof values.trigger.parameterId === "number") {
      TagsService.getTag(values.trigger.parameterId)
        .then(checklist => {
          const updatedEntities = checklist.requirements.map(r => ({ value: r.type, label: r.type }));
          setEntityItems(updatedEntities);
  
          if (values.trigger.entityName && !updatedEntities.some(e => e.value === values.trigger.entityName)) {
            dispatch(change(form, "trigger.entityName", null));
          }
        })
        .catch(e => instantFetchErrorHandler(dispatch, e));
    } else {
      setEntityItems(ChecklistsTriggers.includes(values?.trigger?.type)
        ? TagableEntities
        : AllEntities);
    }
  }, [values.trigger.parameterId]);

  const entityNotRequired = [...ChecklistsTriggers, "On demand"].includes(values.trigger.type);

  return (
    <div className="pt-2 centeredFlex mb-2">
      <FormField
        type="select"
        name="trigger.type"
        label="Trigger type"
        className="flex-fill"
        items={
          isScheduleOrOnDemand && isInternal
          ? [{ label: "On demand", value: "On demand" }, { label: "Schedule", value: "Schedule" }]
          : TriggerTypeItems
        }
        disabled={(isInternal && !isScheduleOrOnDemand) || (values.trigger.type === "On demand" && values.entity)}
        onChange={onTriggerChange}
        debounced={false}
        required
        autoWidth
      />

      {
        ChecklistsTriggers.includes(values.trigger.type) && (
          <FormField
            type="select"
            name="trigger.parameterId"
            label="Checklist"
            className="pl-2 flex-fill"
            disabled={isInternal}
            items={checklists}
            selectValueMark="id"
            selectLabelMark="title"
            allowEmpty
          />
        )
      }

      {enableEntityNameField && (
        <FormField
          type="select"
          name="trigger.entityName"
          label="Entity name"
          required={!entityNotRequired}
          className="pl-2 flex-fill"
          disabled={isInternal}
          items={entityItems}
          allowEmpty={entityNotRequired}
          sort
        />
      )}

      {values.trigger.type === "On edit" && values.trigger.entityName
        && (
          <FormField
            type="select"
            name="trigger.entityAttribute"
            label="Entity attribute"
            className="pl-2 flex-fill"
            disabled={isInternal}
            items={Object.keys(Entities[values.trigger.entityName]).map(mapSelectItems)}
            allowEmpty
          />
      )}

      {values.trigger.type === "Schedule" && (
        <>
          <FormField
            type="select"
            name="trigger.cron.scheduleType"
            label={`Schedule type ${(timeZone ? '(' + timeZone + ' time)' : '' )}`}
            items={ScheduleTypeItems}
            required
            className="pl-2 flex-fill"
            autoWidth
          />

          {values.trigger.cron.scheduleType === "Custom" && (
            <FormField
              type="text"
              name="trigger.cron.custom"
              label="Cron Schedule"
              className="pl-2 flex-fill"
              labelAdornment={(
                <span>
                  <a target="_blank" href="http://www.cronmaker.com/" rel="noreferrer">
                    <IconButton
                      classes={{
                        root: "inputAdornmentButton"
                      }}
                    >
                      <Help className="inputAdornmentIcon" />
                    </IconButton>
                  </a>
                </span>
              )}
              required
            />
          )}
        </>
      )}
    </div>
  );
};

export default TriggerCardContent;
