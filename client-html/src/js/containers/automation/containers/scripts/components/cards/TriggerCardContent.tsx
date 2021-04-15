/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import IconButton from "@material-ui/core/IconButton";
import Help from "@material-ui/icons/Help";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { AQL_ENTITY_ITEMS } from "../../../../constants";

const TriggerCardContent = props => {
  const {
    TriggerTypeItems, ScheduleTypeItems, enableEntityNameField, values, isInternal
  } = props;

  const isNew = !values.id;

  const isScheduleOrOnDemand = useMemo(() => Boolean(
    (values.trigger && values.trigger.type === "On demand") || (values.trigger && values.trigger.type === "Schedule")
  ), [
    values.trigger,
    values.trigger && values.trigger.type
  ]);

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
        required
        disabled={!isNew && !isScheduleOrOnDemand}
        autoWidth
      />

      {enableEntityNameField && (
        <FormField
          type="select"
          name="entity"
          label="Entity name"
          required={values && values.trigger && values.trigger.type !== "On demand"}
          className="pl-2"
          disabled={isInternal}
          items={AQL_ENTITY_ITEMS}
          allowEmpty={!(values && values.trigger && values.trigger.type !== "On demand")}
        />
      )}

      {values && values.trigger && values.trigger.type === "Schedule" && (
        <>
          <FormField
            type="select"
            name="trigger.cron.scheduleType"
            label="Schedule type"
            items={ScheduleTypeItems}
            required
            className="pl-2"
            autoWidth
          />

          {values && values.trigger && values.trigger.cron && values.trigger.cron.scheduleType === "Custom" && (
            <FormField
              type="text"
              name="trigger.cron.custom"
              label="Cron Schedule"
              className="pl-2"
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
