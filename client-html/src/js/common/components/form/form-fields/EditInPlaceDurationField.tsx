/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useState } from "react";
import EditInPlaceField from "./EditInPlaceField";
import { formatDurationMinutes } from "../../../utils/dates/formatString";

const parseDurationString = (duration: string): number => {
  if (duration.indexOf(".") > -1) {
    return Math.round(parseFloat(duration) * 60);
  }

  let min = 0;
  const hoursMatch = duration.match(/(\d+)h/);
  const minutesMatch = duration.match(/(\d+)m/);
  const plainHours = parseFloat(duration);

  if (hoursMatch) {
    min += Number(hoursMatch[1]) * 60;
  }

  if (minutesMatch) {
    min += Number(minutesMatch[1]);
  }

  if (!hoursMatch && !minutesMatch && !isNaN(plainHours)) {
    min += Math.round(plainHours) * 60;
  }

  return min;
};

const EditInPlaceDurationField: React.FunctionComponent<any> = props => {
  const {
 input, type, defaultValue, ...restProps
} = props;

  const [textValue, setTextValue] = useState(formatDurationMinutes(input.value || 0));

  const onChange = useCallback(
    e => {
      setTextValue(e.target.value);
    },
    [input.onChange]
  );

  const onBlur = useCallback(
    value => {
      const parsed = parseDurationString(value);
      input.onBlur(parsed);
      setTextValue(formatDurationMinutes(parsed || 0));
    },
    [input.onBlur]
  );

  useEffect(() => {
    setTextValue(formatDurationMinutes(input.value || defaultValue || 0));
  }, [input.value, defaultValue]);

  return (
    <EditInPlaceField
      {...restProps}
      defaultValue={defaultValue}
      input={{
        value: textValue,
        onBlur,
        onChange,
        onFocus: input.onFocus
      }}
    />
  );
};

export default EditInPlaceDurationField;
