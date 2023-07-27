/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  useCallback, useEffect, useMemo, useRef, useState
} from "react";
import { ContactInsight, ContactInteraction, EmailTemplate } from "@api/model";
import NumberFormat from "react-number-format";
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
import Button from "@mui/material/Button";
import LoadingButton from '@mui/lab/LoadingButton';
import Collapse from "@mui/material/Collapse";
import { CircularProgress } from "@mui/material";
import Close from "@mui/icons-material/Close";
import Mail from "@mui/icons-material/Mail";
import clsx from "clsx";
import PhoneIcon from "@mui/icons-material/Phone";
import { formatDistanceStrict } from "date-fns";
import AvatarRenderer from "../AvatarRenderer";
import { openInternalLink } from "../../../../../common/utils/links";
import { DD_MM_YYYY_SLASHED } from  "ish-ui";
import { makeAppStyles } from "ish-ui";
import { stubFunction } from "../../../../../common/utils/common";
import EditInPlaceField from  "ish-ui";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { getPhoneMask } from "../../../../../constants/PhoneMasks";
import EditInPlaceDateTimeField from  "ish-ui";
import { useAppDispatch, useAppSelector } from "../../../../../common/utils/hooks";
import { getPluralSuffix } from "../../../../../common/utils/strings";
import { countLines } from "../../../../../common/utils/DOM";
import HoverLink from "../../../../../common/components/layout/HoverLink";
import ContactsService from "../../services/ContactsService";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import NotesService from "../../../../../common/components/form/notes/services/NotesService";
import SendMessageEditView from "../../../messages/components/SendMessageEditView";
import { getUserPreferences, openSendMessage } from "../../../../../common/actions";
import { EMAIL_FROM_KEY } from "../../../../../constants/Config";
import EmailTemplateService from "../../../../automation/containers/email-templates/services/EmailTemplateService";

const useStyles = makeAppStyles(theme => ({
  closeIcon: {
    position: "absolute",
    right: theme.spacing(2),
    top: theme.spacing(2)
  },
  box: {
    width: '100%',
    backgroundColor: theme.palette.background.paper,
    borderRadius: theme.spacing(1),
    border: "2px solid",
    borderColor: theme.palette.divider,
    padding: theme.spacing(3)
  }
}));

const PhoneLabel = ({ phone, label }) => (
  <div className="mb-1">
    <PhoneIcon fontSize="inherit" className="vert-align-mid" />
    {" "}
    <Typography component="span" variant="body2">
      <NumberFormat
        value={phone}
        displayType="text"
        type="text"
        format={getPhoneMask(phone)}
      />
    </Typography>
    {" "}
    <Typography component="span" variant="caption" color="textSecondary">
      {label}
    </Typography>
  </div>
);

const MailLabel = ({ mail, label }) => (
  <div className="mb-1">
    <Mail fontSize="inherit" className="vert-align-mid" />
    {" "}
    <Typography component="span" variant="body2">
      {mail}
    </Typography>
    {" "}
    <Typography component="span" variant="caption" color="textSecondary">
      {label}
    </Typography>
  </div>
);

const getEntityLabel = (entity: string, name: string, currencySymbol?: string) => {
  switch (entity) {
    case "Message": {
      return (
        <span>
          {name}
          {' '}
          <span className="fontWeight600">message </span>
        </span>
      );
    }
    case "Document": {
      return (
        <span>
          {name}
          {' '}
          <span className="fontWeight600">document </span>
          {' '}
          added
        </span>
      );
    }
    case "Voucher":
    case "Membership":
    case "Article": {
      return (
        <span>
          <span className="fontWeight600">Purchased </span>
          {name}
        </span>
      );
    }
    case "Payslip": {
      return (
        <span>
          <span className="fontWeight600">Tutor pay </span>
          processed
        </span>
      );
    }
    case "Survey":
      return (
        <span>
          <span className="fontWeight600">Feedback </span>
          submitted for
          {' '}
          {name}
        </span>
      );
    case "Certificate":
      return (
        <span>
          <span className="fontWeight600">Certificate </span>
          in
          {' '}
          {name}
          {' '}
          created
        </span>
      );
    case "AssessmentSubmission":
      return (
        <span>
          Submitted
          <span className="fontWeight600"> assessment </span>
          for
          {' '}
          {name}
        </span>
      );
    case "Quote":
      return (
        <span>
          {name}
          {' '}
          <span className="fontWeight600">quote </span>
          created
        </span>
      );
    case "Invoice":
      return (
        <span className="money">
          {currencySymbol}
          {name}
          {' '}
          <span className="fontWeight600">invoice </span>
          created
        </span>
      );
    case "PaymentIn":
      return (
        <span className="money">
          {currencySymbol}
          {name}
          {' '}
          <span className="fontWeight600">payment</span>
        </span>
      );
    case "PaymentOut":
      return (
        <span className="money">
          {currencySymbol}
          {name}
          {' '}
          <span className="fontWeight600">refund </span>
          received
        </span>
      );
    case "Note":
      return (
        <span>
          <span className="fontWeight600">Note </span>
          added by 
          {' '}
          {name}
        </span>
      );
    case "Application":
      return (
        <span>
          <span className="fontWeight600">Applied for </span>
          {' '}
          {name}
        </span>
      );
    case "WaitingList":
      return (
        <span>
          Joined
          <span className="fontWeight600"> waiting list </span>
          for 
          {' '}
          {name}
        </span>
      );
    case "Lead":
      return (
        <span>
          <span className="fontWeight600">Lead </span>
          created
        </span>
      );
    case "Enrolment":
      return (
        <span>
          <span className="fontWeight600">Enrolled </span>
          in
          {' '}
          {name}
        </span>
      );  
    default:
      return null;
  }
};

const getInteractionLink = (interaction: ContactInteraction) => {
  switch (interaction.entity) {
    case "Voucher":
    case "Membership":
    case "Article":
      return `/sale/${interaction.id}`;
    case "Note":
      return null;
    default: 
      return `/${interaction.entity[0].toLowerCase()}${interaction.entity.slice(1)}/${interaction.id}`;
  }
};

const Interaction = (interaction: ContactInteraction & { currencySymbol?: string }) => {
  const [clamped, setClamped] = useState(true);
  const [descriptionLines, setSescriptionLines] = useState(null);
  
  const hiddenDescriptionRef = useRef<any>();

  useEffect(() => {
    setSescriptionLines(countLines(hiddenDescriptionRef.current));
  }, [hiddenDescriptionRef.current]);
  
  return (
    <ListItem className="align-items-start pl-0 pr-0">
      <ListItemText
        primary={(
          <Stack spacing={2} direction="row" className="mb-2">
            <HoverLink link={getInteractionLink(interaction)}>
              <div className="text-truncate text-nowrap">
                {getEntityLabel(interaction.entity, interaction.name, interaction.currencySymbol)}
              </div>
            </HoverLink>
            <div className="flex-fill" />
            <Typography variant="caption" color="textSecondary" className="text-nowrap">
              {formatDistanceStrict(new Date(interaction.date), new Date(), { addSuffix: true })}
            </Typography>
          </Stack>
      )}
        secondary={interaction.description ? (
          <Box
            sx={{
              width: '100%', bgcolor: "action.hover", padding: 2
            }}
          >
            <Box
              sx={{
                position: "absolute",
                zIndex: -1
              }}
              ref={hiddenDescriptionRef}
            >
              {interaction.description}
            </Box>
            <Box
              sx={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                display: '-webkit-box',
                lineClamp: clamped ? 2 : "unset",
                WebkitLineClamp: clamped ? 2 : "unset",
                WebkitBoxOrient: 'vertical',
              }}
            >
              {interaction.description}
            </Box>
            {descriptionLines > 2 && (
              <div className="text-end cursor-pointer" onClick={() => setClamped(prev => !prev)}>
                [
                {clamped ? "more" : "less"}
                ]
              </div>
            )}
          </Box>
      ) : null}
        primaryTypographyProps={{ fontSize: "15px" }}
        secondaryTypographyProps={{ fontSize: "13px" }}
      />
    </ListItem>
);
};

interface Props {
  id: number;
  onClose: any;
}

const SHOW_FIRST = 8;

const ContactInsight = (
  {
    id,
    onClose
 }: Props
) => {
  const [addNote, setAddNote] = useState<boolean>(false);
  const [showLast, setShowLast] = useState<boolean>(false);
  const [data, setData] = useState<ContactInsight>(null);
  const [noteLoading, setNoteLoading] = useState<boolean>(false);
  const [noteValue, setNoteValue] = useState<string>("");
  const [dateValue, setDateValue] = useState<string>(null);
  const [emailTemplates, setEmailTemplates] = useState<EmailTemplate[]>(null);

  const currencySymbol = useAppSelector(state => state.currency.shortCurrencySymbol);
  const recepients = useAppSelector(state => state.list.recepients);
  const emailFrom = useAppSelector(state => state.userPreferences[EMAIL_FROM_KEY]);
  const fetchPending = useAppSelector(state => state.fetch.pending);
  const sendMessageOpened = useAppSelector(state => state.sendMessage.open);

  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getUserPreferences([EMAIL_FROM_KEY]));

    EmailTemplateService.getEmailTemplatesWithKeyCode("Contact")
      .then(setEmailTemplates)
      .catch(res => instantFetchErrorHandler(dispatch, res));
  }, []);

  const updateInsight = () => {
    setData(null);
    ContactsService.getInsight(id)
      .then(setData)
      .catch(res => instantFetchErrorHandler(dispatch, res));
  };

  useEffect(() => {
    id ? updateInsight() : setData(null);
    return () => setData(null);
  }, [id]);

  const onSendNote = () => {
    const newNote = {
     message: noteValue, entityName: "Contact", entityId: id, interactionDate: dateValue
    };

    setNoteLoading(true);

    NotesService
      .validateCreate("Contact", id, newNote)
      .then(() => NotesService.create("Contact", id, newNote))
      .catch(res => instantFetchErrorHandler(dispatch, res))
      .finally(() => {
        setNoteLoading(false);
        setAddNote(false);
        setNoteValue("");
        setDateValue(null);
        updateInsight();
      });
  };

  const onSendMessage = () => {
    dispatch(openSendMessage());
  };

  const classes = useStyles();

  const Avatar = useCallback(aProps => (
    <AvatarRenderer
      meta={{}}
      input={{
        value: data.profilePicture
      }}
      showConfirm={() => {}}
      email={data?.email}
      {...aProps}
      twoColumn
      disabled
    />
  ), [data]);
  
  const onNoteFocus = () => {
    if (!addNote) {
      setAddNote(true);
      setDateValue(new Date().toISOString());
    }
  };

  const firstInteractions = useMemo(() => data?.interactions
    .slice(0, SHOW_FIRST)
    .map((int, n) => <Interaction key={n + int.id} {...int} currencySymbol={currencySymbol} />), 
    [data?.interactions, currencySymbol]);

  const lastInteractions = useMemo(() => data?.interactions
    .slice(SHOW_FIRST)
    .map((int, n) => <Interaction key={SHOW_FIRST + n + int.id} {...int} currencySymbol={currencySymbol} />),
    [data?.interactions, currencySymbol]);

  const hasLastInteractions = Boolean(lastInteractions?.length);

  return (
    <div className="relative w-100">
      <SendMessageEditView
        selection={[String(id)]}
        templates={emailTemplates}
        recipientsMessageData={recepients}
        emailFrom={emailFrom}
        listSearchQuery={null}
        filteredCount={1}
        submitting={fetchPending}
        opened={sendMessageOpened}
        listEntity="Contact"
      />

      { data ? (
        <AppBarContainer
          opened={false}
          Avatar={Avatar}
          title={(
            <div className="centeredFlex">
              {data.fullName}
              <IconButton size="small" color="primary" onClick={() => openInternalLink(`/contact/${id}`)}>
                <Launch fontSize="inherit" />
              </IconButton>
            </div>
          )}
          actions={(
            <IconButton size="large" onClick={onClose}>
              <Close />
            </IconButton>
          )}
          hideHelpMenu
          hideSubmitButton
          disableInteraction
        >
          <div className="mt-3 pt-1">
            <Stack spacing={1} direction="row" className="mt-4 mb-2">
              <Chip
                label="Create lead"
                className="fontWeight600"
                onClick={() => openInternalLink(`/lead/new?contactId=${id}&contactName=${data.fullName}`)}
              />
              <Chip
                label="Create application"
                className="fontWeight600"
                onClick={() => openInternalLink(`/application/new?contactId=${id}&contactName=${data.fullName}`)}
              />
              <Chip
                label="Receive payment"
                className="fontWeight600"
                onClick={() => openInternalLink(`/checkout?contactId=${id}`)}
              />
              <Chip
                label="Create sale"
                className="fontWeight600"
                onClick={() => openInternalLink(`/checkout?contactId=${id}`)}
              />
            </Stack>
            <Divider className="mt-4 mb-4" />
            <Grid container columnSpacing={3} rowSpacing={2}>
              <Grid item sm={12} md={4}>
                <div className={classes.box}>
                  <div className="centeredFlex mb-2">
                    <div className="heading flex-fill">Overview</div>
                    <Typography variant="caption" sx={{ fontSize: "10px" }} color="textSecondary">
                      first seen
                      {' '}
                      {formatDistanceStrict(new Date(data.overview.firstSeen || undefined), new Date(), { addSuffix: true })}
                    </Typography>
                  </div>

                  <Stack spacing={2} direction="row" className="mt-2 mb-2">
                    <div className="pr-3">
                      <Typography variant="h6" className="fontWeight600" sx={{ color: "success.light" }}>
                        {currencySymbol}
                        {data.overview.spent}
                      </Typography>
                      <Typography variant="caption">spent</Typography>
                    </div>
                    <div>
                      <Typography variant="h6" className="fontWeight600" sx={{ color: "error.main" }}>
                        {currencySymbol}
                        {data.overview.owing}
                      </Typography>
                      <Typography variant="caption">owing</Typography>
                    </div>
                  </Stack>
                  <Divider />
                  <Stack spacing={2} direction="row" className="mt-2 mb-2" divider={<Divider orientation="vertical" flexItem />}>
                    <HoverLink link={data.overview.enrolments.length ? `/enrolment?search=id in (${data.overview.enrolments})` : null}>
                      <Typography variant="caption" className="lh-1">
                        <span className="fontWeight600">{data.overview.enrolments.length}</span>
                        {" "}
                        enrolments
                      </Typography>
                    </HoverLink>
                  </Stack>
                  <Divider />
                  <Stack spacing={2} direction="row" className="mt-2 mb-2" divider={<Divider orientation="vertical" flexItem />}>
                    <HoverLink link={data.overview.openApplications.length ? `/application?search=id in (${data.overview.openApplications})` : null}>
                      <Typography variant="caption" className="lh-1">
                        <span className="fontWeight600">{data.overview.openApplications.length}</span>
                        {" "}
                        open application
                        {getPluralSuffix(data.overview.openApplications.length)}
                      </Typography>
                    </HoverLink>
                    <HoverLink link={data.overview.closeApplications.length ? `/application?search=id in (${data.overview.closeApplications})` : null}>
                      <Typography variant="caption" className="lh-1">
                        <span className="fontWeight600">{data.overview.closeApplications.length}</span>
                        {" "}
                        closed
                      </Typography>
                    </HoverLink>
                  </Stack>
                  <Divider />
                  <Stack spacing={2} direction="row" className="mt-2 mb-2" divider={<Divider orientation="vertical" flexItem />}>
                    <HoverLink link={data.overview.openLeads.length ? `/lead?search=id in (${data.overview.openLeads})` : null}>
                      <Typography variant="caption" className="lh-1">
                        <span className="fontWeight600">{data.overview.openLeads.length}</span>
                        {" "}
                        open lead
                        {getPluralSuffix(data.overview.openLeads.length)}
                      </Typography>
                    </HoverLink>
                    <HoverLink link={data.overview.closeLeads.length ? `/lead?search=id in (${data.overview.closeLeads})` : null}>
                      <Typography variant="caption" className="lh-1">
                        <span className="fontWeight600">{data.overview.closeLeads.length}</span>
                        {" "}
                        closed
                      </Typography>
                    </HoverLink>
                  </Stack>
                </div>

                <div className={clsx("mt-3", classes.box)}>
                  <div className="heading mb-2">Contact</div>
                  {data.workPhone && <PhoneLabel label="work" phone={data.workPhone} />}
                  {data.homePhone && <PhoneLabel label="home" phone={data.homePhone} />}
                  {data.mobilePhone && <PhoneLabel label="mobile" phone={data.mobilePhone} />}
                  {data.fax && <PhoneLabel label="fax" phone={data.fax} />}
                  {data.email && <MailLabel label="email" mail={data.email} />}
                  <Chip label="Send Message" className="fontWeight600 mt-1" onClick={onSendMessage} />
                </div>
              </Grid>
              <Grid item sm={12} md={8}>
                <div className={classes.box}>
                  <Typography className="heading mb-3" gutterBottom>Activity</Typography>
                  <Box
                    sx={{
                      width: '100%', borderRadius: 1, border: "1px solid", borderColor: "divider"
                    }}
                    className="pt-1 pb-1 p-2"
                  >
                    <Stack spacing={0} direction="column">
                      <div className="d-flex">
                        <EditInPlaceField
                          placeholder="Click here to add a note"
                          className="pr-2 flex-fill"
                          meta={{}}
                          input={{
                            onChange: e => setNoteValue(e.target.value),
                            onFocus: onNoteFocus,
                            onBlur: stubFunction,
                            value: noteValue
                          }}
                          multiline
                        />
                      </div>
                      <Collapse in={addNote}>
                        <Box component="div" sx={{ pt: 4 }}>
                          <Stack spacing={2} direction="row" className="mb-1">
                            <EditInPlaceDateTimeField
                              meta={{
                                dispatch
                              }}
                              input={{
                                onChange: v => setDateValue(v),
                                onFocus: stubFunction,
                                onBlur: stubFunction,
                                value: dateValue
                              }}
                              type="datetime"
                              label="Date"
                              formatDateTime={DD_MM_YYYY_SLASHED}
                            />
                            <EditInPlaceDateTimeField
                              meta={{
                                dispatch
                              }}
                              input={{
                                onChange: v => setDateValue(v),
                                onFocus: stubFunction,
                                onBlur: stubFunction,
                                value: dateValue
                              }}
                              label="Time"
                              type="time"
                            />
                            <div className="flex-fill" />
                            <div className="d-flex">
                              <LoadingButton
                                loading={noteLoading}
                                onClick={onSendNote}
                                type="submit"
                                variant="contained"
                                color="primary"
                                size="small"
                                className="mt-auto"
                                disableElevation
                              >
                                Save
                              </LoadingButton>
                            </div>
                          </Stack>
                        </Box>
                      </Collapse>
                    </Stack>
                  </Box>
                  <Box component="div" className="mt-1">
                    <List sx={{ width: '100%', padding: 0 }}>
                      {firstInteractions}
                    </List>
                    {hasLastInteractions && (
                      <Collapse in={showLast} mountOnEnter>
                        <List sx={{ width: '100%', padding: 0 }}>
                          {lastInteractions}
                        </List>
                      </Collapse>
                    )}
                  </Box>
                  {hasLastInteractions && (
                    <Box component="div" className="d-flex justify-content-center">
                      <Button variant="text" color="primary" sx={{ textTransform: "initial" }} size="small" onClick={() => setShowLast(prev => !prev)}>
                        View 
                        {' '}
                        {lastInteractions.length} 
                        {' '}
                        {showLast ? "less" : "more"}
                      </Button>
                    </Box>
                  )}
                </div>
              </Grid>
            </Grid>
          </div>

        </AppBarContainer>
      ) : (
        <div className="w-100 h-100 d-flex justify-content-center align-items-center">
          <CircularProgress size={40} thickness={5} />
        </div>
      )}
    </div>
);
};

export default ContactInsight;
