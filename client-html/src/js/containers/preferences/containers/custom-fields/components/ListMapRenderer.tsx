/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataType } from '@api/model';
import Delete from '@mui/icons-material/Delete';
import { Typography } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import clsx from 'clsx';
import { AddButton, EditInPlaceField, stubFunction, useHoverShowStyles } from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { useCallback, useState } from 'react';
import { WrappedFieldProps } from 'redux-form';
import { validateSingleMandatoryField } from '../../../../../common/utils/validation';

interface Props {
  label: string;
  dataType: DataType;
  onKeyPress: any;
}

const getDefaultFields = value => {
  let result = [];

  if (value) {
    try {
      result = JSON.parse(value);

      if (Array.isArray(result)) {
        result = result.filter(v => !v.value.includes("*"));
      }
    } catch (e) {
      console.error(e);
    }
  }
  return result;
};

const ListMapRenderer: React.FC<WrappedFieldProps & Props> = props => {
  const {
    label, dataType, input: { value, onChange, name }, meta: { error }, onKeyPress
  } = props;

  const { classes: hoverClasses } = useHoverShowStyles();
  const [fields, setFields] = useState(getDefaultFields(value));

  const debounceChange = useCallback<any>(debounce(v => {
      const initial = value ? JSON.stringify(value) : [];
      const updated = dataType === "List" && initial.includes("*") ? [...v, { value: "*" }] : v;
      onChange(updated.length ? JSON.stringify(updated) : null);
    }, 600), [value]);

  const onAdd = () => {
    const updated = [...fields, dataType === "List" ? { value: "" } : { value: "", label: "" }];
    setFields(updated);
    debounceChange(updated);
  };

  const onDelete = index => {
    const updated = [...fields];
    updated.splice(index, 1);
    setFields(updated);
    debounceChange(updated);
  };

  const changeField = useCallback<any>((index, key, value) => {
    const updated = [...fields];
    updated[index][key] = value;
    setFields(updated);
    debounceChange(updated);
  }, [fields]);

  return (
    <div id={name}>
      <div>
        <div className="centeredFlex">
          <Typography component="div" variant="caption" color="textSecondary" noWrap>
            {label}
          </Typography>
          <AddButton onClick={onAdd} className="p-0-5" />
        </div>
        {error && (
          <Typography className="shakingError" component="div" variant="caption" color="error" noWrap>
            {error}
          </Typography>
        )}

        <Typography variant="body1" component="div">
          <div>
            <ul className="m-0 pl-3">
              {
                fields.map((f, index) => {
                  const valueError = validateSingleMandatoryField(f.value);
                  const labelError = validateSingleMandatoryField(f.label);
                  const isMap = dataType === "Map";

                  return (
                    <li key={index} className={hoverClasses.container}>
                      <Typography variant="body2" color="inherit" component="span">
                        <EditInPlaceField
                          meta={{
                          error: isMap ? labelError : valueError,
                          invalid: Boolean(isMap ? labelError : valueError)
                        }}
                          input={{
                          onChange: e => changeField(index, isMap ? "label" : "value", e.target.value),
                          onFocus: stubFunction,
                          onBlur: stubFunction,
                          value: (isMap ? f.label : f.value) || ""
                        }}
                          inline
                          onKeyPress={onKeyPress}
                          multiline
                        />

                        {
                          isMap && (
                          <>
                            {" ( "}
                            <EditInPlaceField
                              meta={{
                                error: valueError,
                                invalid: Boolean(valueError)
                              }}
                              input={{
                                onChange: e => changeField(index, "value", e.target.value),
                                onFocus: stubFunction,
                                onBlur: stubFunction,
                                value: f.value || ""
                              }}
                              onKeyPress={onKeyPress}
                              inline
                              multiline
                            />
                            {" ) "}
                          </>
                        )
                      }
                      </Typography>
                      <IconButton
                        onClick={() => onDelete(index)}
                        className={clsx(hoverClasses.target, "p-0-5  vert-align-mid")}
                      >
                        <Delete className="editInPlaceIcon" />
                      </IconButton>
                    </li>
                  );
                })
              }
            </ul>
          </div>
        </Typography>
      </div>
    </div>
);
};

export default ListMapRenderer;
