/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect, useState } from "react";
import EditInPlaceField from "./EditInPlaceField";
import { formatDurationMinutes } from "../../utils/dates/formatString";
import { EditInPlaceFieldProps, FieldInputProps, FieldMetaProps } from "../model/Fields";

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

function EditInPlaceDurationField<FP extends FieldInputProps, MP extends FieldMetaProps>(
{
 input, ...restProps
}: EditInPlaceFieldProps<FP, MP>) {
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
    setTextValue(formatDurationMinutes(input.value || 0));
  }, [input.value]);

  return (
    <EditInPlaceField
      {...restProps}
      input={{
        value: textValue,
        onBlur,
        onChange,
        onFocus: input.onFocus,
        name: input.name
      }}
    />
  );
}

export default EditInPlaceDurationField;
