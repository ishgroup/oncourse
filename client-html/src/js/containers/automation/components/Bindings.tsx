/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import Typography from "@material-ui/core/Typography";
import Delete from "@material-ui/icons/Delete";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import { Binding } from "@api/model";
import {
 arrayPush, arrayRemove, Field, FieldArray
} from "redux-form";
import { Dispatch } from "redux";
import makeStyles from "@material-ui/core/styles/makeStyles";
import clsx from "clsx";
import { CommonListItem } from "../../../model/common/sidebar";
import { SelectItemDefault } from "../../../model/entities/common";
import { IMPORT_TEMPLATES_FORM_NAME } from "../containers/import-templates/ImportTemplates";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "../containers/scripts/constants";
import DataTypesMenu from "./DataTypesMenu";
import DataTypeRenderer from "../../../common/components/form/DataTypeRenderer";
import { YYYY_MM_DD_MINUSED } from "../../../common/utils/dates/format";
import { renderAutomationItems } from "../utils";

export type BindingsItemType = "component" | "label";

interface BindingsItemProps {
  item: Binding;
  type: BindingsItemType;
  field?: string;
  onDelete?: any;
  index?: string;
  emailTemplateItems?: SelectItemDefault[];
}

const useStyles = makeStyles(theme => ({
  labelTypeWrapper: {
    maxHeight: "24px"
  },
  itemLabel: {
    marginTop: theme.spacing(0.5)
  },
  checkboxDelete: {
    marginBottom: theme.spacing(0.25),
    marginLeft: theme.spacing(-2.25)
  },
  textDelete: {
    marginLeft: `-${theme.spacing(1) - 1}px`
  },
  dateDelete: {
    marginBottom: theme.spacing(1.25)
  }
}));

const BindingsItem = React.memo<BindingsItemProps>(({
    item, index, onDelete, type, field, emailTemplateItems
  }) => {
  const classes = useStyles({});

  const buttonClass = useMemo(() => {
    switch (item.type) {
      default:
      case "Text": {
        return clsx("mb-1", classes.textDelete);
      }
      case "Checkbox": {
        return classes.checkboxDelete;
      }
      case "Date":
      case "Date time": {
        return classes.dateDelete;
      }
    }
  }, [item.type]);

  const fieldProps: any = useMemo(() => {
    const props = {};

    if (["Checkbox", "Money"].includes(item.type)) {
      props["stringValue"] = true;
    }

    if (item.type === "Date") {
      props["formatValue"] = YYYY_MM_DD_MINUSED;
    }

    if (item.type === "Message template") {
      props["items"] = emailTemplateItems;
      props["selectLabelCondition"] = renderAutomationItems;
    }

    return props;
  }, [item, emailTemplateItems]);

  return type === "label" ? (
    <>
      {item.label && (
        <Typography variant="caption" color="textSecondary" className={classes.itemLabel}>
          {item.label}
        </Typography>
      )}
      <Typography
        variant="body2"
        component="div"
        className={clsx("centeredFlex pb-0-5", classes.labelTypeWrapper)}
      >
        <span>
          {item.name}
          {" "}
          <Typography variant="caption" color="textSecondary">
            (
            {item.type}
            )
          </Typography>
        </span>

        {onDelete && (
          <IconButton className="lightGrayIconButton" role={index} onClick={onDelete}>
            <Delete fontSize="inherit" />
          </IconButton>
        )}
      </Typography>
    </>
  ) : (
    <div className="d-flex align-items-end">
      <Field
        label={item.name}
        name={`${field}.value`}
        type={item.type}
        component={DataTypeRenderer}
        fullWidth
        {...fieldProps}
      />

      {onDelete && (
        <IconButton className={clsx("lightGrayIconButton", buttonClass)} role={index} onClick={onDelete}>
          <Delete fontSize="inherit" />
        </IconButton>
      )}
    </div>
  );
});

interface BindingsProps {
  name: string;
  label: string;
  form: string;
  disabled?: boolean;
  defaultVariables?: { type: string; name: string }[];
  dispatch: Dispatch;
  itemsType: BindingsItemType;
  emailTemplates?: CommonListItem[];
}

const Bindings = React.memo<BindingsProps>( props => {
  const {
    name, defaultVariables, dispatch, form, label, itemsType, disabled, emailTemplates
  } = props;

  const [anchorEl, setAnchorEl] = React.useState(null);

  const isOptionsBindingType = useMemo(() => (name === "options"), [name]);
  const isVariablesBindingType = useMemo(() => (name === "variables"), [name]);
  const isImportAutomation = useMemo(() => (form === IMPORT_TEMPLATES_FORM_NAME), [form]);
  const isScriptsAutomation = useMemo(() => (form === SCRIPT_EDIT_VIEW_FORM_NAME), [form]);
  const emailTemplateItems = useMemo(
    () => (emailTemplates
      ? emailTemplates.filter(t => t.keyCode).map(t => ({
        value: t.keyCode, label: t.name, hasIcon: t.hasIcon, id: t.id,
      }))
      : []), [emailTemplates],
  );

  const handleClick = useCallback(event => {
    setAnchorEl(event.currentTarget);
  }, []);

  const handleClose = useCallback(() => {
    setAnchorEl(null);
  }, []);

  const handleAdd = useCallback(
    val => {
      dispatch(arrayPush(form, name, val));
    },
    [form, name]
  );

  const handleDelete = useCallback(
    event => {
      const index = event.currentTarget.getAttribute("role");

      dispatch(arrayRemove(form, name, Number(index)));
    },
    [form, name]
  );

  const itemsRenderer = useCallback(
    props => {
      const { fields } = props;

      return fields.map((i, n) => (
        <BindingsItem
          type={itemsType}
          key={n}
          field={i}
          item={fields.get(n)}
          index={String(n)}
          onDelete={!disabled && handleDelete}
          emailTemplateItems={emailTemplateItems}
        />
      ));
    },
    [disabled, handleDelete, itemsType, name, form, emailTemplateItems]
  );

  return (
    <div>
      <DataTypesMenu
        anchorEl={anchorEl}
        handleClose={handleClose}
        handleAdd={handleAdd}
        dispatch={dispatch}
        itemsType={itemsType}
        isOptionsBindingType={isOptionsBindingType}
        isVariablesBindingType={isVariablesBindingType}
        isImportAutomation={isImportAutomation}
        isScriptsAutomation={isScriptsAutomation}
      />

      <div className="centeredFlex pb-1">
        <div className="heading">{label}</div>
        {!disabled && (
          <IconButton className="p-0 ml-1" onClick={handleClick}>
            <AddCircle className="addButtonColor" width={20} />
          </IconButton>
        )}
      </div>

      {defaultVariables && defaultVariables.map((i, n) => <BindingsItem key={n} item={i as Binding} type="label" />)}

      <FieldArray name={name} component={itemsRenderer} rerenderOnEveryChange/>
    </div>
  );
});

export default Bindings;
