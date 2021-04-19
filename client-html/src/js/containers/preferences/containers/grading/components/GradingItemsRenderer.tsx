/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import clsx from "clsx";
import React from "react";
import { Typography } from "@material-ui/core";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import Delete from "@material-ui/icons/Delete";
import { WrappedFieldArrayProps } from "redux-form";
import { GradingItem } from "@api/model";
import { useHoverShowStyles } from "../../../../../common/styles/hooks";
import FormField from "../../../../../common/components/form/form-fields/FormField";

const GradingItemsRenderer: React.FC<WrappedFieldArrayProps<GradingItem> & {classes: any}> = (
  props
) => {
  const hoverClasses = useHoverShowStyles();

  const {
    fields,
    meta: { error },
    classes
  } = props;

  console.log(props);

  const onAdd = () => {
    fields.push({
      id: null,
      created: null,
      modified: null,
      name: null,
      lowerBound: null
    });
  };

  return (
    <div id={fields.name} className={classes.gradingItemsRoot}>
      <div className="centeredFlex">
        <Typography component="div" variant="caption" color="textSecondary" noWrap>
          Grading items
        </Typography>
        <IconButton onClick={onAdd} className="p-0-5">
          <AddCircle className="addButtonColor" />
        </IconButton>
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
              fields.map((f, index) => (
                <li className={hoverClasses.container}>
                  <Typography variant="body2" color="inherit" className="centeredFlex" noWrap>
                    <FormField
                      type="text"
                      name={`${f}.name`}
                      formatting="inline"
                      className="mr-1"
                      required
                    />
                    <FormField
                      type="number"
                      name={`${f}.lowerBound`}
                      formatting="inline"
                      hideArrows
                      required
                    />

                    <IconButton
                      onClick={() => fields.remove(index)}
                      className={clsx(hoverClasses.target, "p-0-5 d-inline-flex vert-align-mid")}
                    >
                      <Delete className="editInPlaceIcon" />
                    </IconButton>
                  </Typography>
                </li>
                ))
            }
          </ul>
        </div>
      </Typography>
    </div>
  );
};

export default GradingItemsRenderer as any;
