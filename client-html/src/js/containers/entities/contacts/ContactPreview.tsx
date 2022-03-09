/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  Dispatch, useCallback, useEffect, useState
} from "react";
import { connect } from "react-redux";
import { Field, getFormValues, reduxForm } from "redux-form";
import Launch from "@mui/icons-material/Launch";
import IconButton from "@mui/material/IconButton";
import Chip from "@mui/material/Chip";
import Box from "@mui/material/Box";
import Stack from "@mui/material/Stack";
import Divider from "@mui/material/Divider";
import Grid from "@mui/material/Grid";
import ListItemText from "@mui/material/ListItemText";
import Typography from "@mui/material/Typography";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import MuiAvatar from '@mui/material/Avatar';
import Button from "@mui/material/Button";
import PhoneCallbackOutlinedIcon from "@mui/icons-material/PhoneCallbackOutlined";
import CollectionsBookmarkOutlinedIcon from "@mui/icons-material/CollectionsBookmarkOutlined";
import Collapse from "@mui/material/Collapse";
import { State } from "../../../reducers/state";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import FullScreenStickyHeader
  from "../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import AvatarRenderer from "./components/AvatarRenderer";
import { getContact } from "./actions";
import { getContactFullName } from "./utils";
import { openInternalLink } from "../../../common/utils/links";
import CustomSelector, { CustomSelectorOption } from "../../../common/components/custom-selector/CustomSelector";
import FormSubmitButton from "../../../common/components/form/FormSubmitButton";
import FormField from "../../../common/components/form/formFields/FormField";
import { DD_MM_YYYY_SLASHED } from "../../../common/utils/dates/format";

const ContactPreview: React.FC<any> = props => {
  const {
    values,
    dispatch,
    form,
    selected,
    getContactRecord,
  } = props;

  const [addNote, setAddNote] = useState<boolean>(false);

  useEffect(() => {
    if (selected.id) getContactRecord(selected.id);
  }, [selected]);

  const Avatar = useCallback(aProps => (
    <Field
      name="profilePicture"
      label="Profile picture"
      component={AvatarRenderer}
      showConfirm={() => {}}
      email={values.email}
      twoColumn
      props={{
        dispatch,
        form,
        avatarSize: 60
      }}
      {...aProps}
    />
  ), [values?.email]);

  const addNoteOptions: CustomSelectorOption[] = [
    { caption: "Inbound phone call", body: "Inbound phone call", type: null },
    { caption: "Outbound phone call", body: "Outbound phone call", type: null },
    { caption: "Inbound email", body: "Inbound email", type: null },
    { caption: "In person", body: "In person", type: null },
    { caption: "Chat", body: "Chat", type: null },
  ];

  return values ? (
    <div>
      <div className="d-flex pr-4">
        <FullScreenStickyHeader
          opened={false}
          twoColumn={false}
          Avatar={Avatar}
          title={(
            <div className="centeredFlex">
              {values && !values.isCompany && values.title && values.title.trim().length > 0 ? `${values.title} ` : ""}
              {values ? (!values.isCompany ? getContactFullName(values) : values.lastName) : ""}
              <IconButton size="small" color="primary" onClick={() => openInternalLink(`/contact/${values.id}`)}>
                <Launch fontSize="inherit" />
              </IconButton>
            </div>
          )}
          disableInteraction
        />
      </div>
      <Stack spacing={2} direction="row" className="mt-4 mb-2">
        <Chip label="Create lead" onClick={() => {}} />
        <Chip label="Create application" onClick={() => {}} />
        <Chip label="Receive payment" onClick={() => {}} />
        <Chip label="Create sale" onClick={() => {}} />
        <Chip label="Send Message" onClick={() => {}} />
      </Stack>
      <Divider className="mt-4 mb-4" />
      <Grid container columnSpacing={3} rowSpacing={2}>
        <Grid item sm={12} md={4}>
          <Box
            sx={{
              width: '100%', bgcolor: 'background.paper', borderRadius: 2, border: "1px solid", borderColor: "divider"
            }}
            className="p-3"
          >
            <Typography className="heading" gutterBottom>Overview</Typography>
            <Stack spacing={2} direction="row" className="mt-2 mb-2">
              <div className="pr-3">
                <Typography variant="body1" className="fontWeight600" sx={{ color: "success.main" }}>$1350</Typography>
                <Typography variant="caption">spent</Typography>
              </div>
              <div>
                <Typography variant="body1" className="fontWeight600" color="error">$250</Typography>
                <Typography variant="caption">owing</Typography>
              </div>
            </Stack>
            <Divider className="mt-2 mb-2" />
            <Typography variant="caption">
              <span className="fontWeight600">4</span>
              {" "}
              enrolments
            </Typography>
            <Divider className="mt-2 mb-2" />
            <Typography variant="caption">
              <span className="fontWeight600">2</span>
              {" "}
              applications
            </Typography>
            <Divider className="mt-2 mb-2" />
            <Stack spacing={2} direction="row" className="mt-2 mb-2" divider={<Divider orientation="vertical" flexItem />}>
              <Typography variant="caption" className="lh-1">
                <span className="fontWeight600">1</span>
                {" "}
                open lead
              </Typography>
              <Typography variant="caption" className="lh-1">
                <span className="fontWeight600">2</span>
                {" "}
                closed
              </Typography>
            </Stack>
          </Box>
        </Grid>
        <Grid item sm={12} md={8}>
          <Box
            sx={{
              width: '100%', bgcolor: 'background.paper', borderRadius: 2, border: "1px solid", borderColor: "divider"
            }}
            className="p-3"
          >
            <Typography className="heading mb-3" gutterBottom>Activity</Typography>
            <Stack spacing={2} direction="row" className="mt-2 mb-2" divider={<Divider orientation="vertical" flexItem />}>
              <Typography variant="caption" className="lh-1">
                <span className="fontWeight600">14</span>
                {" "}
                interactions
              </Typography>
              <Typography variant="caption" className="lh-1">
                <span className="fontWeight600">06/02/2022</span>
                {" "}
                last contacted
              </Typography>
              <Typography variant="caption" className="lh-1">
                <span className="fontWeight600">2</span>
                {" "}
                inactive days
              </Typography>
            </Stack>
            <Divider className="mt-3 mb-3" />
            <Box
              sx={{
                width: '100%', borderRadius: 1, border: "1px solid", borderColor: "divider"
              }}
              className="pt-1 pb-1 p-2"
            >
              <Stack spacing={0} direction="column">
                <div className="d-flex">
                  <FormField
                    type="multilineText"
                    name="note"
                    placeholder="Click here to add a note"
                    fieldClasses={{ text: "fs-13" }}
                    className="pr-2 flex-fill"
                    onClick={() => setAddNote(!addNote)}
                    isInline
                    hideLabel
                  />
                  <CustomSelector
                    caption=""
                    options={addNoteOptions}
                    onSelect={() => {}}
                    initialIndex={0}
                  />
                </div>
                <Collapse in={addNote}>
                  <Box component="div" sx={{ pt: 4 }}>
                    <Stack spacing={2} direction="row" className="mb-1">
                      <FormField
                        type="date"
                        name="date"
                        label="Date"
                        formatDate={DD_MM_YYYY_SLASHED}
                      />
                      <FormField
                        type="time"
                        name="time"
                        label="Time"
                      />
                      <div className="flex-fill" />
                      <div>
                        <FormSubmitButton disabled={false} invalid={false} fab />
                      </div>
                    </Stack>
                  </Box>
                </Collapse>
              </Stack>
            </Box>
            <Box component="div" className="mt-1">
              <List sx={{ width: '100%' }}>
                <ListItem className="align-items-start pl-0 pr-0">
                  <ListItemAvatar>
                    <MuiAvatar sx={{ bgcolor: theme => theme.palette.grey[50] }}>
                      <PhoneCallbackOutlinedIcon color="secondary" fontSize="small" />
                    </MuiAvatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={(
                      <Stack spacing={2} direction="row" className="mb-2">
                        <Typography variant="caption" className="flex-fill">
                          <span className="fontWeight600">Ari</span>
                          {" "}
                          received Inbound phone call
                        </Typography>
                        <Typography variant="caption" color="secondary">
                          2 days ago
                        </Typography>
                      </Stack>
                    )}
                    secondaryTypographyProps={{ component: "div" }}
                    secondary={(
                      <Box
                        sx={{
                          width: '100%', borderColor: "divider", bgcolor: theme => theme.palette.grey[50]
                        }}
                        className="p-2"
                      >
                        Called to enquire about group training. Presented what we could do for them. Sounds interested. Scheduled a meeting for next week to discuss in more detail with management.
                      </Box>
                    )}
                  />
                </ListItem>
                <ListItem className="align-items-start pl-0 pr-0">
                  <ListItemAvatar>
                    <MuiAvatar sx={{ bgcolor: theme => theme.palette.grey[50] }}>
                      <CollectionsBookmarkOutlinedIcon color="secondary" fontSize="small" />
                    </MuiAvatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={(
                      <Stack spacing={2} direction="row" className="mb-2">
                        <Typography variant="caption" className="flex-fill">
                          <span className="fontWeight600">Enrolled</span>
                          {" "}
                          in Woodworking WDWK
                        </Typography>
                        <Typography variant="caption" color="secondary">
                          3 weeks ago
                        </Typography>
                      </Stack>
                    )}
                    secondaryTypographyProps={{ component: "div" }}
                    secondary={(
                      <Box
                        sx={{
                          width: '100%', borderColor: "divider", bgcolor: theme => theme.palette.grey[50]
                        }}
                        className="p-2"
                      >
                        <Stack spacing={2} direction="row" divider={<Divider orientation="vertical" flexItem />}>
                          <span>
                            <Typography variant="caption" className="lh-1 mr-0-5">
                              Invoice #
                            </Typography>
                            <Typography variant="caption" className="lh-1" sx={{ borderBottom: "1px solid" }} color="secondary">
                              1295
                            </Typography>
                          </span>
                          <span>
                            <Typography variant="caption" className="lh-1 mr-0-5">
                              Class
                            </Typography>
                            <Typography variant="caption" className="lh-1" sx={{ borderBottom: "1px solid" }} color="secondary">
                              Woodworking WDWK-5
                            </Typography>
                          </span>
                        </Stack>
                      </Box>
                    )}
                  />
                </ListItem>
              </List>
            </Box>
            <Box component="div" className="d-flex justify-content-center mb-3">
              <Button variant="text" color="primary" sx={{ textTransform: "initial" }} size="small">View 9 more</Button>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </div>
  ) : null;
};

const mapStateToProps = (state: State) => ({
  values: getFormValues(LIST_EDIT_VIEW_FORM_NAME)(state),
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getContactRecord: (id: number) => dispatch(getContact(id)),
});

export default reduxForm<any, any>({
  form: "ContactPreviewForm"
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ContactPreview));
