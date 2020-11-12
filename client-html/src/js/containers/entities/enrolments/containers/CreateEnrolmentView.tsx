/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import { Dispatch } from "redux";
import { Field, InjectedFormProps, reduxForm } from "redux-form";
import { connect } from "react-redux";
import IconButton from "@material-ui/core/IconButton";
import Close from "@material-ui/icons/Close";
import Search from "@material-ui/icons/Search";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { ColumnWidth } from "@api/model";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import { ListSideBarDefaultWidth } from "../../../../common/components/list-view/ListView";
import ResizableWrapper from "../../../../common/components/layout/resizable/ResizableWrapper";
import Drawer from "../../../../common/components/layout/Drawer";
import LoadingIndicator from "../../../../common/components/layout/LoadingIndicator";
import Content from "../../../../common/components/layout/Content";
import { updateColumnsWidth } from "../../../preferences/actions";
import { FormTextField } from "../../../../common/components/form/form-fields/TextField";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";

export const FORM: string = "CREATE_ENROLMENT_VIEW_FORM";

interface Props extends InjectedFormProps {
  leftColumnWidth: number;
  updateColumnsWidth: (columnsWidth: ColumnWidth) => void;
}

const CreateEnrolmentViewForm = React.memo<Props>(props => {
  const {
 change, invalid, dirty, handleSubmit, leftColumnWidth, updateColumnsWidth
} = props;

  const [sidebarWidth, setSidebarWidth] = useState(leftColumnWidth || ListSideBarDefaultWidth);
  const [focusedItem, setFocusedItem] = useState(null);

  useEffect(() => {
    if (sidebarWidth !== leftColumnWidth) {
      setSidebarWidth(leftColumnWidth);
    }
  }, [leftColumnWidth]);

  const handleResizeCallback = useCallback(
    (...props) => {
      setSidebarWidth(props[2].getClientRects()[0].width);
    },
    [sidebarWidth]
  );

  const handleResizeStopCallback = useCallback(
    (...props) => {
      updateColumnsWidth(props[2].getClientRects()[0].width);
    },
    [sidebarWidth]
  );

  const handleFocusCallback = useCallback(props => {
    // console.log(props.target.name);
    //  setFocusedItem
  }, []);

  const onSubmit = useCallback(props => {
    // console.log(props);
  }, []);

  const onCloseClick = useCallback(() => {
    // close event
  }, []);

  return (
    <div>
      <form autoComplete="off" noValidate onSubmit={handleSubmit(onSubmit)} className="root">
        <ResizableWrapper
          onResizeStop={handleResizeStopCallback}
          onResize={handleResizeCallback}
          sidebarWidth={sidebarWidth}
        >
          <Drawer>
            <div className="pl-3 pr-3 pt-3">
              <HeaderField
                heading="Contacts"
                name="contacts"
                placeholder="Enter contact..."
                onFocus={handleFocusCallback}
                items={[]}
                change={change}
              />

              <HeaderField
                heading="Items"
                name="items"
                placeholder="Enter course or item..."
                onFocus={handleFocusCallback}
                items={[]}
                change={change}
              />

              <div className="centeredFlex">
                <div className="heading mt-2 mb-2">Payments</div>
              </div>
              <FormField type="text" name="payments" label="" hideLabel onFocus={handleFocusCallback} />
            </div>
          </Drawer>
        </ResizableWrapper>

        <div className="appFrame">
          <LoadingIndicator />
          <CustomAppBar>
            <Grid container className="flex-fill">
              <Typography variant="body2">Select an item from list below</Typography>
            </Grid>
            <Button onClick={onCloseClick} className="closeAppBarButton">
              Cancel
            </Button>
            <Button
              type="submit"
              classes={{
                root: "whiteAppBarButton",
                disabled: "whiteAppBarButtonDisabled"
              }}
              disabled={invalid || !dirty}
            >
              Save
            </Button>
          </CustomAppBar>
          <Content />
        </div>
      </form>
    </div>
  );
});

const HeaderField = ({
 change, heading, items, name, placeholder, onFocus
}) => {
  const [listItem, setListItem] = useState(items || []);
  const [search, setSearch] = useState(null);

  const onTextChange = useCallback(
    (value, props) => {
      setSearch(value);
    },
    [search]
  );

  const onClear = useCallback(
    props => {
      setSearch(null);
      change(name, "");
    },
    [search]
  );

  return (
    <>
      <div className="heading mt-3">{heading}</div>
      {listItem.length > 0 && <div className="mt-2 mb-2">{listItem}</div>}
      <Field
        name={name}
        placeholder={placeholder}
        component={FormTextField}
        onFocus={onFocus}
        onChange={onTextChange}
        InputProps={{
          startAdornment: <Search className="inputAdornmentIcon textSecondaryColor mr-1" />,
          endAdornment: search && (
            <IconButton className="closeAndClearButton" onClick={onClear}>
              <Close className="inputAdornmentIcon" />
            </IconButton>
          )
        }}
        fullWidth
      />
    </>
  );
};

const mapStateToProps = (state: State) => ({
  leftColumnWidth: state.preferences.columnWidth && state.preferences.columnWidth.preferenceLeftColumnWidth
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    updateColumnsWidth: (preferenceLeftColumnWidth: number) => {
      dispatch(updateColumnsWidth({ preferenceLeftColumnWidth }));
    }
  });

const CreateEnrolmentView = reduxForm({
  form: FORM
})(CreateEnrolmentViewForm);

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CreateEnrolmentView);
