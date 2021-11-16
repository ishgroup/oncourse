/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import Dialog from "@mui/material/Dialog";
import Grid from "@mui/material/Grid";
import {
  change, Field, getFormValues, reduxForm, Validator
} from "redux-form";
import { connect } from "react-redux";
import Slide from "@mui/material/Slide";
import Typography from "@mui/material/Typography";
import { TransitionProps } from "@mui/material/transitions";
import IconButton from "@mui/material/IconButton";
import LockOpen from "@mui/icons-material/LockOpen";
import Lock from "@mui/icons-material/Lock";
import { Tag, TagStatus } from "@api/model";
import FormField from "../../../common/components/form/formFields/FormField";
import { FormEditorField } from "../../../common/components/markdown-editor/FormEditor";
import { validateSingleMandatoryField, validateTagName } from "../../../common/utils/validation";
import ColorPicker from "../../../common/components/color-picker/ColorPicker";
import { State } from "../../../reducers/state";
import { NoArgFunction } from "../../../model/common/CommonFunctions";
import { usePrevious } from "../../../common/utils/hooks";
import { mapSelectItems } from "../../../common/utils/common";
import { onSubmitFail } from "../../../common/utils/highlightFormClassErrors";
import AppBarContainer from "../../../common/components/layout/AppBarContainer";

const tagStatusValues = Object.keys(TagStatus).map(mapSelectItems);

const Transition = React.forwardRef<unknown, TransitionProps>((props, ref) => (
  <Slide direction="up" ref={ref} {...props as any} />
));

const editableFields = ["name", "status", "urlPath", "color", "content"];

interface Props {
  open?: boolean;
  values?: Tag;
  dispatch?: any;
  handleSubmit?: any;
  invalid?: boolean;
  dirty?: boolean;
  parent?: string;
  system?: boolean;
  validateName: Validator;
  validateRootName: Validator;
  validateShortName: Validator;
  onClose: NoArgFunction;
}

const TagItemEditView = React.memo<Props>(props => {
  const {
    open,
    values,
    dispatch,
    handleSubmit,
    invalid,
    dirty,
    parent,
    validateName,
    validateRootName,
    validateShortName,
    onClose
  } = props;

  const [lockedUrl, setLockedUrl] = useState(true);

  const prevId = usePrevious(values.id);

  useEffect(() => {
    if (prevId !== values.id) {
      if (values.urlPath && lockedUrl) {
        setLockedUrl(false);
      }

      if (!values.urlPath && !lockedUrl) {
        setLockedUrl(true);
      }
    }
  }, [values.urlPath, lockedUrl, prevId, values.id]);

  const onSave = useCallback(
    val => {
      if (parent) dispatch(change("TagsForm", parent, val));
      else {
        editableFields.forEach(f => {
          dispatch(change("TagsForm", f, val[f]));
        });
      }

      onClose();
    },
    [parent]
  );

  const onCloseClick = useCallback(() => onClose(), []);

  const onSubmit = useMemo(() => handleSubmit(onSave), [handleSubmit, onSave]);

  const onLockClick = useCallback(() => {
    if (!lockedUrl) {
      dispatch(change("TagItemForm", "urlPath", null));
    }

    if (lockedUrl) {
      dispatch(change("TagItemForm", "urlPath", values.name));
    }

    setLockedUrl(prev => !prev);
  }, [values.name, lockedUrl]);

  const validateNameWithPath = useCallback((value, values, props) => validateName(value, values, props, parent), [
    parent
  ]);

  const validateShortNameWithPath = useCallback((value, values) => validateShortName(value, values, parent), [parent]);

  return (
    <Dialog fullScreen open={open} TransitionComponent={Transition}>
      <form onSubmit={onSubmit} autoComplete="off">
        <AppBarContainer
          disabled={!dirty}
          invalid={invalid}
          title={(!values.name || values.name.trim().length === 0) ? "New" : values && values.name.trim()}
          disableInteraction={values.system}
          noDrawer
          hideHelpMenu
          fields={(
            <Grid item xs={8}>
              <FormField
                type="text"
                name="name"
                label="Name"
                validate={[
                  validateSingleMandatoryField,
                  parent ? validateNameWithPath : validateRootName,
                  validateTagName
                ]}
                disabled={values.system}
              />
            </Grid>
          )}
          onCloseClick={onCloseClick}
        >
          <Grid container>
            <Grid item xs={12} sm={4}>
              <FormField
                type="select"
                name="status"
                label="Visibility"
                items={tagStatusValues}
                disabled={values.system}
                required
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <FormField
                type="text"
                name="urlPath"
                label="URL path"
                disabled={lockedUrl}
                placeholder={values.name}
                normalize={val => {
                  if (!val) return null;
                  return val;
                }}
                validate={validateShortNameWithPath}
                labelAdornment={(
                  <span>
                    <IconButton className="inputAdornmentButton" onClick={onLockClick}>
                      {!lockedUrl && <LockOpen className="inputAdornmentIcon" />}
                      {lockedUrl && <Lock className="inputAdornmentIcon" />}
                    </IconButton>
                  </span>
                )}
                hidePlaceholderInEditMode
              />
            </Grid>

            <Grid item xs={12} sm={4} className="centeredFlex">
              <Typography className="pr-1" variant="caption" color="textSecondary" component="span">
                Color
              </Typography>
              <Field name="color" component={ColorPicker} />
            </Grid>

            <Grid item xs={12} sm={8}>
              <FormEditorField name="content" label="Details" />
            </Grid>
          </Grid>
        </AppBarContainer>
      </form>
    </Dialog>
  );
});

const mapStateToProps = (state: State) => ({
  values: getFormValues("TagItemForm")(state),
  open: state.tags.editView.open,
  parent: state.tags.editView.parent
});

export default (reduxForm({
  form: "TagItemForm",
  initialValues: {},
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, null)(TagItemEditView)) as unknown) as React.FC<Props>;
