/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Binding, DataType } from '@api/model';
import Button from '@mui/material/Button';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import Grow from '@mui/material/Grow';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Popper from '@mui/material/Popper';
import { makeAppStyles, NoArgFunction } from 'ish-ui';
import React, { useCallback, useEffect, useRef } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { Form, getFormValues, initialize, InjectedFormProps, reduxForm } from 'redux-form';
import FormField from '../../../common/components/form/formFields/FormField';
import { BindingsItemType } from './Bindings';

const useStyles = makeAppStyles()(theme => ({
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

const DataTypes = Object.keys(DataType).filter(k => !["Portal subdomain","Record", "Pattern text", "List", "Map", "Long text", "URL", "Email"].includes(k));

interface BindingEditPopupProps extends InjectedFormProps {
  popupAnchorEl: any;
  onCancel: any;
  itemsType: BindingsItemType;
  onSave?: (val: Binding) => void;
  values?: Binding;
}

const BindingEditPopupBase = React.memo<BindingEditPopupProps>(
  ({
     popupAnchorEl, onCancel, onSave, handleSubmit, invalid, values, reset
  }) => {
    const nameRef = useRef<any>();
    const popperRef = useRef<any>();

    const { classes }  = useStyles();

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
        nameRef.current.focus();
      }
    }, [popupAnchorEl, nameRef.current]);

    useEffect(() => {
      if (popperRef.current) {
        popperRef.current.forceUpdate();
      }
    }, [values, popperRef.current]);

    const onSubmit = useCallback(() => {
      onSave(values);
      reset();
      onCancel();
    }, [values]);

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
              <Form onSubmit={handleSubmit(onSubmit)}>
                <DialogContent className="overflow-hidden">
                  <FormField
                    type="text"
                    label="Label"
                    name="label"
                    inputRef={nameRef}
                    className="mb-2"
                    required
                  />

                  <FormField
                    type="text"
                    label="Name"
                    name="name"
                    validate={validateBindingName}
                    required
                  />
                </DialogContent>

                <DialogActions>
                  <Button onClick={onSubmit} color="primary" disabled={invalid}>
                    Save
                  </Button>
                </DialogActions>
              </Form>

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
          // Show Object option only in variables
          if (t === "Object" && !isVariablesBindingType) {
            return null;
          }
          return <MenuItem onClick={handleClick} key={t} role={t}>{t}</MenuItem>;
        })}
      </Menu>
    </>
  );
});

export default DataTypesMenu;
