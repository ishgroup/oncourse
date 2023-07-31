/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import Typography from "@mui/material/Typography";
import Delete from "@mui/icons-material/Delete";
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import IconButton from "@mui/material/IconButton";
import { Binding } from "@api/model";
import {
 arrayPush, arrayRemove, Field, FieldArray
} from "redux-form";
import { Dispatch } from "redux";
import clsx from "clsx";
import { makeStyles } from "@mui/styles";
import Grid from "@mui/material/Grid";
import { SelectItemDefault } from "../../../model/entities/common";
import { IMPORT_TEMPLATES_FORM_NAME } from "../containers/import-templates/ImportTemplates";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "../containers/scripts/constants";
import DataTypesMenu from "./DataTypesMenu";
import DataTypeRenderer from "../../../common/components/form/DataTypeRenderer";
import { YYYY_MM_DD_MINUSED } from  "ish-ui";
import { renderAutomationItems } from "../utils";
import { AppTheme } from  "ish-ui";
import { useHoverShowStyles, AddButton } from  "ish-ui";
import { CatalogItemType } from "../../../model/common/Catalog";

export type BindingsItemType = "component" | "label";

interface BindingsItemProps {
  item: Binding;
  type: BindingsItemType;
  field?: string;
  onDelete?: any;
  infoLink?: string;
  index?: string;
  emailTemplateItems?: SelectItemDefault[];
  gridProps?: any;
  highlightable?: boolean;
  noLabel?: boolean;
}

// @ts-ignore
const useStyles = makeStyles((theme: AppTheme) => ({
  labelTypeWrapper: {
    fontWeight: 400,
    paddingTop: theme.spacing(1),
    maxHeight: "24px",
    "&$highlightable:hover": {
      color: theme.palette.primary.main
    }
  },
  itemLabel: {
    marginTop: theme.spacing(0.5)
  },
  checkboxDelete: {
    marginBottom: theme.spacing(0.25),
    marginLeft: "auto"
  },
  textDelete: {
    // @ts-ignore
    marginLeft: `-${theme.spacing(1) - 1}`,
  },
  dateDelete: {
    marginBottom: theme.spacing(1.25)
  },
  highlightable: {}
}));

const showOnForm = (item, field) => {
  const fieldNodes = document.querySelectorAll(`[id^='${field}.value'],[id^='input-${field}.value']`);

  if (item.type === "Checkbox") {
    fieldNodes.forEach(node => {
      node.closest("label")?.classList.add("animated", "shake", "primaryColor");
      node.closest("label")?.querySelector("svg")?.classList.add("primaryColor");
    });

    setTimeout(() => {
      fieldNodes.forEach(node => {
        node.closest("label")?.classList.remove("animated", "shake", "primaryColor");
        node.closest("label")?.querySelector("svg")?.classList.remove("primaryColor");
      });
    }, 1000);
    return;
  }

  fieldNodes.forEach(node => {
    node.querySelector("label")?.classList.add("primaryColor");
    node.querySelector("input")?.classList.add("primaryColor");
    node.classList.add("animated", "shake", "primaryColor");
  });

  setTimeout(() => {
    fieldNodes.forEach(node => {
      node.querySelector("label")?.classList.remove("primaryColor");
      node.querySelector("input")?.classList.remove("primaryColor");
      node.classList.remove("animated", "shake", "primaryColor");
    });
  }, 1000);
};

const BindingsItem = React.memo<BindingsItemProps>(({
    item, index, onDelete, infoLink, type, field, emailTemplateItems, highlightable, noLabel, gridProps = {}
  }) => {
  const classes = useStyles();
  const hoverClasses = useHoverShowStyles();

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
      props["itemRenderer"] = renderAutomationItems;
      props["valueRenderer"] = renderAutomationItems;
    }

    return props;
  }, [item, emailTemplateItems]);

  return type === "label" ? (
    <Grid item xs={12} className={clsx("centeredFlex", hoverClasses.container)}>
      <div className="flex-fill w-100">
        {!noLabel && item.label && (
          <Typography
            variant="caption"
            color="textSecondary"
            component="div"
            className={clsx("text-truncate text-nowrap", classes.itemLabel)}
          >
            {item.label}
          </Typography>
        )}
        <Typography
          variant="body1"
          component="div"
          onMouseEnter={highlightable ? () => showOnForm(item, field) : null}
          className={clsx("centeredFlex pb-0-5", classes.labelTypeWrapper, highlightable && classes.highlightable)}
        >
          <span className="w-100 centeredFlex">
            <span className="text-truncate text-nowrap">{item.name}</span>
            {" "}
            <Typography variant="caption" color="textSecondary" noWrap>
              (
              {item.type}
              )
            </Typography>
            {infoLink && (
              <IconButton size="small" role={index} onClick={() => window.open(infoLink, "_blank")}>
                <HelpOutlineIcon fontSize="inherit" />
              </IconButton>
            )}
          </span>
        </Typography>
      </div>
      {onDelete && (
        <IconButton size="small" className={clsx("lightGrayIconButton", hoverClasses.target)} role={index} onClick={onDelete}>
          <Delete fontSize="inherit" />
        </IconButton>
      )}
    </Grid>
  ) : (
    <Grid item xs={6} {...gridProps}>
      <Field
        label={item.label || item.name}
        name={`${field}.value`}
        type={item.type}
        component={DataTypeRenderer}
        {...fieldProps}
      />
    </Grid>
  );
});

export const BindingsRenderer = props => {
    const {
     fields, disabled, handleDelete, itemsType, emailTemplates, highlightable, noLabel
    } = props;

  const emailTemplateItems = useMemo(
    () => (emailTemplates
      ? emailTemplates.filter(t => t.keyCode).map(t => ({
        value: t.keyCode, label: t.title, hasIcon: t.keyCode.startsWith("ish."), id: t.id,
      }))
      : []), [emailTemplates],
  );

  return fields.map((i, n) => (
    <BindingsItem
      type={itemsType}
      key={n}
      field={i}
      item={fields.get(n)}
      index={String(n)}
      onDelete={!disabled && handleDelete}
      emailTemplateItems={emailTemplateItems}
      highlightable={highlightable}
      noLabel={noLabel}
    />
  ));
};

const getInfoLink = (type: string) => {
  if (!type || type === "Context") {
    return null;
  }

  return `https://www.ish.com.au/onCourse/doc/dsl/#${type}`;
};

interface BindingsProps {
  name: string;
  label: string;
  form: string;
  disabled?: boolean;
  defaultVariables?: { type: string; name: string }[];
  dispatch: Dispatch;
  itemsType: BindingsItemType;
  emailTemplates?: CatalogItemType[];
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

  // @ts-ignore
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

      <Grid container>
        {defaultVariables && (
          <Grid item xs={12} className="mb-3">
            <Typography variant="caption">Built in variables</Typography>
            {defaultVariables.map((i, n) => <BindingsItem key={n} item={i as Binding} type="label" infoLink={getInfoLink(i.type)} />)}
          </Grid>
        )}
        <Grid item xs={12} className="centeredFlex pb-1">
          <Typography variant="caption">{label}</Typography>
          {!disabled && (
            <AddButton className="p-0 ml-1" onClick={handleClick} />
          )}
        </Grid>
        <FieldArray 
          name={name} 
          component={BindingsRenderer}
          disabled={disabled} 
          handleDelete={handleDelete} 
          itemsType="label"
          emailTemplates={emailTemplates}
          highlightable={isOptionsBindingType}
          noLabel={isOptionsBindingType}
          rerenderOnEveryChange 
        />
      </Grid>
    </div>
  );
});

export default Bindings;
