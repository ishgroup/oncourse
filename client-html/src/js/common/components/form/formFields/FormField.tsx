/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { useMemo } from "react";
import { Field } from "redux-form";
import { validateSingleMandatoryField, validateTagsList } from "../../../utils/validation";
import { FormFieldWrapperProps } from "../../../../model/common/Fields";
import FormFieldBase from "./FormFieldBase";

const FormField = React.forwardRef<any, FormFieldWrapperProps>((props, ref) => {
  const { name,
    validate,
    type,
    ...rest
  } = props;

  const tags = type === "tags" ? props.tags : [];
  const required = type !== "stub" ? props.required : false;

  const validateResolver = useMemo(() => {
    const result = [];
    if (required) {
      result.push(validateSingleMandatoryField);
    }
    if (validate) {
      result.push(validate);
    }
    if (type === "tags") {
      result.push(
        (value, allValues, formProps) => validateTagsList(tags || [], value, allValues, formProps, (type === "tags"
            ? props.validateEntity
            : null
        ))
      );
    }
    return result.length > 1 ? result : result.length ? result[0] : undefined;
  }, [validate, required, type, tags, type === "tags" && props.validateEntity]);

  return (
    <Field
      type={type}
      name={name}
      component={FormFieldBase}
      validate={validateResolver}
      props={{
        ref,
        format: rest.format
      }}
      {...rest}
    />
  );
});

export default FormField;