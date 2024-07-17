/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Alert } from "@mui/material";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import MenuItem from "@mui/material/MenuItem";
import clsx from "clsx";
import { addYears, format } from "date-fns";
import { AnyArgFunction, ShowConfirmCaller, validateMinMaxDate, YYYY_MM_DD_MINUSED } from "ish-ui";
import React, { memo, useState } from "react";
import { Form, getFormValues, reduxForm, reset } from "redux-form";
import FormField from "../../../../common/components/form/formFields/FormField";
import { useAppDispatch, useAppSelector } from "../../../../common/utils/hooks";
import { bulkDeleteEntityRecordsRequest } from "../../common/actions";
import { archiveMessages } from "../actions";

const DATE_BEFORE_FORM_NAME = 'DateBeforeForm';

const validateMaxDate = value => validateMinMaxDate(value, "", format(addYears(new Date(), -3), YYYY_MM_DD_MINUSED), "", "Archive date must be more than three years ago from today");

const DateBeforeForm = reduxForm<any, any>({
  form: DATE_BEFORE_FORM_NAME
})(({ open, onClose, invalid, dirty, archiveBefore, handleSubmit }) => {
  return <Dialog
    open={open}
    onClose={onClose}
  >
    <Form onSubmit={handleSubmit}>
      <DialogTitle>Warning</DialogTitle>
      <DialogContent>
        <Alert severity="warning" className="mb-3">
          Messages in the system from before given date will be archived and will not be able to be seen or access in onCourse. They will only be visible within the exported CSV file that can be requested from onCourse support.
        </Alert>
        <FormField
          label="Archive before this date"
          type="date"
          name="archiveBefore"
          validate={validateMaxDate}
        />
      </DialogContent>

      <DialogActions>
        <Button
          onClick={onClose}
          color="primary"
        >
          Cancel
        </Button>
        <Button
          disabled={!dirty || invalid || !archiveBefore}
          variant="contained"
          color="primary"
          type="submit"
        >
          Archive
        </Button>
      </DialogActions>
    </Form>
  </Dialog>;
});

interface QuedMessagesBulkDeleteProps {
  menuItemClass: string;
  closeMenu: AnyArgFunction;
  selection: number[];
  showConfirm: ShowConfirmCaller;
}

const MessagesCogwheelActions = memo<QuedMessagesBulkDeleteProps>(({
  menuItemClass,
  closeMenu,
  showConfirm
}) => {
  const [openArchive, setOpenArchive] = useState(false);

  const archiveBefore = useAppSelector(state => (getFormValues(DATE_BEFORE_FORM_NAME)(state) as any)?.archiveBefore);

  const dispatch = useAppDispatch();

  const onBulkEditClick = () => {
    showConfirm({
      onConfirm: () => {
        dispatch(bulkDeleteEntityRecordsRequest(
          "Message",
          {
            ids: [],
            search: "status is QUEUED ",
            filter: "",
            tagGroups: []
          }));
        closeMenu();
      },
      confirmMessage: "All messages with Queued status will be permanently deleted. This action can not be undone",
      confirmButtonText: "Delete anyway"
    });
  };

  return  <>
    <DateBeforeForm
      open={openArchive}
      onClose={() => {
        setOpenArchive(false);
        dispatch(reset(DATE_BEFORE_FORM_NAME));
      }}
      archiveBefore={archiveBefore}
      onSubmit={() => {
        dispatch(archiveMessages({
          archiveBefore
        }));
        setOpenArchive(false);
        closeMenu();
    }} />
    <MenuItem className={clsx(menuItemClass, "errorColor")} onClick={() => setOpenArchive(true)}>
      Archive old messages
    </MenuItem>
    <MenuItem className={clsx(menuItemClass, "errorColor")} onClick={onBulkEditClick}>
      Bulk delete queued messages
    </MenuItem>
  </>;
});

export default MessagesCogwheelActions;