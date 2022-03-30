/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  useCallback, useEffect, useRef, useState
} from "react";
import { ContactInsight, ContactInteraction } from "@api/model";
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
import Collapse from "@mui/material/Collapse";
import { CircularProgress } from "@mui/material";
import Close from "@mui/icons-material/Close";
import clsx from "clsx";
import PhoneIcon from "@mui/icons-material/Phone";
import { formatDistanceStrict } from "date-fns";
import AvatarRenderer from "../AvatarRenderer";
import { openInternalLink } from "../../../../../common/utils/links";
import { DD_MM_YYYY_SLASHED } from "../../../../../common/utils/dates/format";
import { makeAppStyles } from "../../../../../common/styles/makeStyles";
import { stubFunction } from "../../../../../common/utils/common";
import EditInPlaceField from "../../../../../common/components/form/formFields/EditInPlaceField";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { getPhoneMask } from "../../../../../constants/PhoneMasks";
import EditInPlaceDateTimeField from "../../../../../common/components/form/formFields/EditInPlaceDateTimeField";
import { useAppSelector } from "../../../../../common/utils/hooks";
import { getPluralSuffix } from "../../../../../common/utils/strings";
import { countLines } from "../../../../../common/utils/DOM";
import HoverLink from "../../../../../common/components/layout/HoverLink";

const mock: ContactInsight = {
  email: "palaven@tut.by",
  fullName: "Angel De LaMuerte",
  fax: "0492415186",
  homePhone: "0492415186",
  mobilePhone: "0492415186",
  workPhone: "0492415186",
  profilePicture: null,
  overview: {
    firstSeen: "2018-02-03",
    spent: 2200,
    owing: 130,
    enrolments: [9459, 1245, 1244],
    openApplications: [],
    closeApplications: [],
    openLeads: [],
    closeLeads: []
  },
  interactions: [
    {
      id: 674,
      entity: "Note",
      name: "Ari",
      date: "2022-03-27",
      description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Accusamus at atque consequatur dicta doloremque eligendi explicabo id impedit ipsa ipsam iure modi nesciunt praesentium quaerat quasi sequi tenetur totam, voluptates.\n"
    },
    {
      id: 645674,
      entity: "Message",
      name: "Email",
      date: "2022-03-26",
      description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Accusamus at atque consequatur dicta doloremque eligendi explicabo id impedit ipsa ipsam iure modi nesciunt praesentium quaerat quasi sequi tenetur totam, voluptates.\n"
    },
    {
      id: 679274,
      entity: "Message",
      name: "SMS",
      date: "2022-03-24",
      description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Accusamus at atque consequatur dicta doloremque eligendi explicabo id impedit ipsa ipsam iure modi nesciunt praesentium quaerat quasi sequi tenetur totam, voluptates.\n"
    },
    {
      id: 5653,
      entity: "Application",
      name: "Woodworking for experts",
      date: "2022-03-23",
      description: null
    },
    {
      id: 3567,
      entity: "WaitingList",
      name: "Lathe technique",
      date: "2022-03-20",
      description: null
    },
    {
      id: 37865,
      entity: "Quote",
      name: "$110",
      date: "2022-03-14",
      description: null
    },
    {
      id: 666,
      entity: "Lead",
      name: null,
      date: "2022-03-19",
      description: null
    },
    {
      id: 34543,
      entity: "Enrolment",
      name: "Advanced Diploma of Acting for Contemporary Screen",
      date: "2022-03-14",
      description: null
    },
    {
      id: 9544,
      entity: "Assessment",
      name: "Woodwork",
      date: "2022-03-13",
      description: null
    },
    {
      id: 3467865243,
      entity: "PaymentIn",
      name: "$100",
      date: "2022-03-14",
      description: null
    },
    {
      id: 3427865243,
      entity: "PaymentOut",
      name: "$120",
      date: "2022-03-14",
      description: null
    },
    {
      id: 3427865,
      entity: "Invoice",
      name: "$140",
      date: "2022-03-14",
      description: null
    },
    {
      id: 343568065,
      entity: "Certificate",
      name: "Woodwork Level IV",
      date: "2022-03-11",
      description: null
    },
    {
      id: 568065,
      entity: "Survey",
      name: "Woodwork",
      date: "2022-03-11",
      description: null
    },
    {
      id: 5680,
      entity: "Payslip",
      name: null,
      date: "2022-03-11",
      description: null
    },
    {
      id: 5684560,
      entity: "Sale",
      name: "French handbook",
      date: "2022-03-11",
      description: null
    },
    {
      id: 568466560,
      entity: "Document",
      name: "Tutor resume",
      date: "2022-03-11",
      description: null
    }
  ]
};

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

const getEntityLabel = (entity: string, name: string) => {
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
    case "Sale": {
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
    case "Assessment":
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
        <span>
          {name}
          {' '}
          <span className="fontWeight600">invoice </span>
          created
        </span>
      );
    case "PaymentIn":
      return (
        <span>
          {name}
          {' '}
          <span className="fontWeight600">payment</span>
        </span>
      );
    case "PaymentOut":
      return (
        <span>
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

const Interaction = (interaction: ContactInteraction) => {
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
            <HoverLink link={interaction.entity !== "Note" && `/${interaction.entity[0].toLowerCase()}${interaction.entity.slice(1)}/${interaction.id}`}>
              <div className="text-truncate text-nowrap">
                {getEntityLabel(interaction.entity, interaction.name)}
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
  const [noteValue, setNoteValue] = useState<string>(null);
  const [dateValue, setDateValue] = useState<string>(null);
  
  const currencySymbol = useAppSelector(state => state.currency.shortCurrencySymbol);

  useEffect(() => {
    setTimeout(() => {
      setData(mock);
    }, 1000);
  }, []);

  const classes = useStyles();

  const Avatar = useCallback(aProps => (
    <AvatarRenderer
      meta={{}}
      input={{}}
      showConfirm={() => {}}
      email={data.email}
      {...aProps}
      twoColumn
      disabled
    />
  ), [data?.email]);
  
  const onNoteFocus = () => {
    setAddNote(true);
    setDateValue(new Date().toISOString());
  };

  const firstInteractions = data?.interactions.slice(0, SHOW_FIRST);
  const lastInteractions = data?.interactions.slice(SHOW_FIRST);
  const hasLastInteractions = Boolean(lastInteractions?.length);

  return (
    <div className="relative w-100">
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
              <Chip label="Create lead" className="fontWeight600" onClick={() => {}} />
              <Chip label="Create application" className="fontWeight600" onClick={() => {}} />
              <Chip label="Receive payment" className="fontWeight600" onClick={() => {}} />
              <Chip label="Create sale" className="fontWeight600" onClick={() => {}} />
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
                  <Chip label="Send Message" className="fontWeight600 mt-1" onClick={() => {}} />
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
                              meta={{}}
                              input={{
                                onChange: v => setDateValue(v),
                                onFocus: stubFunction,
                                onBlur: stubFunction,
                                value: dateValue
                              }}
                              type="date"
                              label="Date"
                              formatDate={DD_MM_YYYY_SLASHED}
                            />
                            <EditInPlaceDateTimeField
                              meta={{}}
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
                              <Button
                                type="submit"
                                variant="contained"
                                color="primary"
                                size="small"
                                className="mt-auto"
                                disableElevation
                              >
                                Save
                              </Button>
                            </div>
                          </Stack>
                        </Box>
                      </Collapse>
                    </Stack>
                  </Box>
                  <Box component="div" className="mt-1">
                    <List sx={{ width: '100%', padding: 0 }}>
                      {firstInteractions.map((int, n) => <Interaction key={n + int.id} {...int} />)}
                    </List>
                    {hasLastInteractions && (
                      <Collapse in={showLast} mountOnEnter>
                        <List sx={{ width: '100%', padding: 0 }}>
                          {lastInteractions.map((int, n) => <Interaction key={SHOW_FIRST + n + int.id} {...int} />)}
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
