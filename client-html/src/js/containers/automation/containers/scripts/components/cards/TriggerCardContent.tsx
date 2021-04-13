/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo } from "react";
import IconButton from "@material-ui/core/IconButton";
import Help from "@material-ui/icons/Help";
import * as Entities from "@aql/queryLanguageModel";
import { change } from "redux-form";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { AQL_ENTITY_ITEMS } from "../../../../constants";
import { mapSelectItems } from "../../../../../../common/utils/common";

const TriggerCardContent = props => {
  const {
    TriggerTypeItems, ScheduleTypeItems, enableEntityNameField, values, isInternal, dispatch, form
  } = props;

  const isNew = !values.id;

  const isScheduleOrOnDemand = useMemo(() => Boolean(
    (values.trigger && values.trigger.type === "On demand") || (values.trigger && values.trigger.type === "Schedule")
  ), [
    values.trigger,
    values.trigger && values.trigger.type
  ]);

  useEffect(() => {
    if (values.trigger?.entityAttribute) {
      dispatch(change(form, "trigger.entityAttribute", null));
    }
  }, [values.trigger?.type, values.trigger?.entityName]);

  return (
    <div className="pt-2 centeredFlex">
      <FormField
        type="select"
        name="trigger.type"
        label="Trigger type"
        items={
          isScheduleOrOnDemand && isInternal
          ? [{ label: "On demand", value: "On demand" }, { label: "Schedule", value: "Schedule" }]
          : TriggerTypeItems
        }
        disabled={isInternal && !isScheduleOrOnDemand}
        required
        autoWidth
      />

      {enableEntityNameField && (
        <FormField
          type="select"
          name="trigger.entityName"
          label="Entity name"
          required={values?.trigger?.type !== "On demand"}
          className="pl-2"
          disabled={isInternal}
          items={AQL_ENTITY_ITEMS}
          allowEmpty={!(values?.trigger?.type !== "On demand")}
        />
      )}

      {values?.trigger?.type === "On edit" && values?.trigger?.entityName
        && (
        <FormField
          type="select"
          name="trigger.entityAttribute"
          label="Entity attribute"
          className="pl-2"
          disabled={isInternal}
          items={Object.keys(Entities[values.trigger.entityName]).map(mapSelectItems)}
          allowEmpty
        />
      )}

      {values?.trigger?.type === "Schedule" && (
        <>
          <FormField
            type="select"
            name="trigger.cron.scheduleType"
            label="Schedule type"
            items={ScheduleTypeItems}
            disabled={isInternal}
            required
            className="pl-2"
            autoWidth
          />

          {values?.trigger?.cron?.scheduleType === "Custom" && (
            <FormField
              type="text"
              name="trigger.cron.custom"
              label="Cron Schedule"
              className="pl-2"
              disabled={isInternal}
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
