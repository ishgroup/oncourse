/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useRef } from "react";
import { Binding, DataType } from "@api/model";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import Popper from "@material-ui/core/Popper";
import Grow from "@material-ui/core/Grow";
import Paper from "@material-ui/core/Paper";
import makeStyles from "@material-ui/core/styles/makeStyles";
import {
 getFormValues, initialize, InjectedFormProps, reduxForm
} from "redux-form";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import FormField from "../../../common/components/form/form-fields/FormField";
import { BindingsItemType } from "./Bindings";
import { NoArgFunction } from "../../../model/common/CommonFunctions";

const useStyles = makeStyles(theme => ({
  popper: {
    zIndex: theme.zIndex.modal + 1
  },
  paper: {
    minWidth: "280px",
    "&:after": {
      top: "50%",
      right: "0px",
      width: "10px",
      height: "22px",
      position: "absolute",
      transform: "translateY(-50%)",
      background: theme.palette.background.paper,
      content: "''",
      zIndex: 1
    }
  },
  corner: {
    right: "-6px",
    top: "50%",
    width: "12px",
    height: "12px",
    position: "absolute",
    transform: "translateY(-50%) rotate(45deg)",
    background: theme.palette.background.paper,
    boxShadow: "1px 0px 2px 0px rgba(0,0,0,0.2), 1px 0px 2px -1px rgba(0,0,0,0.12), 0px 0px 1px 0px rgba(0,0,0,0.12);"
  }
}));

const DataTypes = Object.keys(DataType).filter(k => !["Record", "Pattern text", "List", "Map", "Long text", "URL", "Email"].includes(k));

interface BindingEditPopupProps extends InjectedFormProps {
  popupAnchorEl: any;
  onCancel: any;
  itemsType: BindingsItemType;
  onSave?: (val: Binding) => void;
  values?: Binding;
}

const BindingEditPopupBase = React.memo<BindingEditPopupProps>(
  ({
 popupAnchorEl, onCancel, onSave, handleSubmit, invalid, itemsType, values, reset
}) => {
    const nameRef = useRef<any>();
    const popperRef = useRef<any>();

    const classes = useStyles({});

    const setPopperRef = useCallback(node => {
      if (node) {
        popperRef.current = node;
      }
    }, []);

    const validateBindingName = useCallback(
      value => (value.match(/[^0-9a-zA-Z_\s]/g) ? "Forbidden symbol" : undefined),
      []
    );

    useEffect(() => {
      if (popupAnchorEl && nameRef.current) {
        nameRef.current.edit();
      }
    }, [popupAnchorEl, nameRef.current]);

    useEffect(() => {
      if (popperRef.current) {
        popperRef.current.update();
      }
    }, [values, popperRef.current]);

    const onSubmit = useCallback(() => {
      onSave(values);
      reset();
      onCancel();
    }, [values]);

    const labelType = itemsType === "label";

    return (
      <Popper
        open={Boolean(popupAnchorEl)}
        anchorEl={popupAnchorEl}
        className={classes.popper}
        popperRef={setPopperRef}
        placement="left"
        transition
      >
        {({ TransitionProps }) => (
          <Grow {...TransitionProps} timeout={200}>
            <Paper className={classes.paper} elevation={8}>
              <form onSubmit={handleSubmit}>
                <DialogContent className="overflow-hidden">
                  {labelType && (
                    <FormField
                      type="text"
                      label="Label"
                      name="label"
                      ref={nameRef}
                      required
                      fullWidth
                    />
                  )}

                  <FormField
                    type="text"
                    label="Name"
                    name="name"
                    validate={validateBindingName}
                    ref={labelType ? undefined : nameRef}
                    required
                    fullWidth
                  />
                </DialogContent>

                <DialogActions>
                  <Button onClick={onSubmit} color="primary" disabled={invalid}>
                    Save
                  </Button>
                </DialogActions>
              </form>

              <div className={classes.corner} />
            </Paper>
          </Grow>
        )}
      </Popper>
    );
  }
);

const BindingEditPopup = reduxForm<any, any>({
  form: "BindingEditForm"
})(
  connect(
    state => ({
      values: getFormValues("BindingEditForm")(state)
    }),
    null
  )(BindingEditPopupBase)
);

interface DataTypesMenuProps {
  anchorEl: HTMLElement;
  handleClose: NoArgFunction;
  handleAdd: (val: Binding) => void;
  dispatch: Dispatch;
  itemsType: BindingsItemType;
  isOptionsBindingType?: boolean;
  isVariablesBindingType?: boolean;
  isImportAutomation?: boolean;
  isScriptsAutomation?: boolean;
}

const DataTypesMenu = React.memo<DataTypesMenuProps>(({
    anchorEl,
    handleClose,
    handleAdd,
    dispatch,
    itemsType,
    isOptionsBindingType,
    isVariablesBindingType,
    isImportAutomation,
    isScriptsAutomation
  }) => {
  const [popupAnchorEl, setPopupAnchorEl] = React.useState(null);

  const handleClick = useCallback(event => {
    const type: DataType = event.currentTarget.getAttribute("role");

    dispatch(initialize("BindingEditForm", { type, value: type === "Checkbox" ? "false" : "" }));

    setPopupAnchorEl(event.currentTarget);
  }, []);

  const onCancel = useCallback(() => {
    handleClose();
    setPopupAnchorEl(null);
  }, []);

  return (
    <>
      <BindingEditPopup popupAnchorEl={popupAnchorEl} onCancel={onCancel} onSave={handleAdd} itemsType={itemsType} />

      <Menu id="data-types-menu" anchorEl={anchorEl} disableEnforceFocus open={Boolean(anchorEl)} onClose={onCancel}>
        {DataTypes.map(t => {
          // Show File option only in Import variables
          if (t === "File" && (!isImportAutomation || !isVariablesBindingType)) {
            return null;
          }
          // Show Message template option only in Script options
          if (t === "Message template" && (!isScriptsAutomation || !isOptionsBindingType)) {
            return null;
          }
          return <MenuItem onClick={handleClick} key={t} role={t}>{t}</MenuItem>;
        })}
      </Menu>
    </>
  );
});

export default DataTypesMenu;
