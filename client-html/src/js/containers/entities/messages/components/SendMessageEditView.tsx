/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import { Binding, DataType, EmailTemplate, MessageType, Recipients, SearchQuery } from '@api/model';
import OpenInNew from '@mui/icons-material/OpenInNew';
import { CardContent, Dialog, FormControlLabel, Grid } from '@mui/material';
import Card from '@mui/material/Card';
import IconButton from '@mui/material/IconButton';
import Slide from '@mui/material/Slide';
import { TransitionProps } from '@mui/material/transitions';
import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import {
  AnyArgFunction,
  NoArgFunction,
  openInternalLink,
  StringArgFunction,
  StyledCheckbox,
  Switch,
  YYYY_MM_DD_MINUSED
} from 'ish-ui';
import debounce from 'lodash.debounce';
// eslint-disable-next-line import/no-extraneous-dependencies
import React, { Fragment, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change, DecoratedFormProps, Field, FieldArray, getFormValues, initialize, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import previewSmsImage from '../../../../../images/preview-sms.png';
import { closeSendMessage, getEmailTemplatesWithKeyCode, getUserPreferences } from '../../../../common/actions';
import instantFetchErrorHandler from '../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import DataTypeRenderer from '../../../../common/components/form/DataTypeRenderer';
import FormField from '../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../common/components/layout/AppBarContainer';
import { clearRecipientsMessageData, getRecipientsMessageData } from '../../../../common/components/list-view/actions';
import LoadingIndicator from '../../../../common/components/progress/LoadingIndicator';
import { getManualLink } from '../../../../common/utils/getManualLink';
import { saveCategoryAQLLink } from '../../../../common/utils/links';
import { validateSingleMandatoryField } from '../../../../common/utils/validation';
import { EMAIL_FROM_KEY } from '../../../../constants/Config';
import { SEND_MESSAGE_FORM_NAME } from '../../../../constants/Forms';
import { MessageData, MessageExtended } from '../../../../model/common/Message';
import { State } from '../../../../reducers/state';
import { sendMessage } from '../actions';
import MessageService from '../services/MessageService';
import { getMessageRequestModel } from '../utils';
import RecipientsSelectionSwitcher from './RecipientsSelectionSwitcher';

const styles = theme => ({
  previewContent: {
    "& table": {
      width: "100%"
    }
  },
  previewSmsWrapper: {
    height: 530
  },
  previewSmsImage: {
    position: "absolute",
    width: "100%",
    height: "100%",
    left: 0,
    right: 0,
    zIndex: 0,
    overflow: "hidden",
    "& > img": {
      position: "relative",
      zIndex: 1,
      transform: "translate(-50%, -50%)",
      top: "50%",
      left: "50%",
      height: "100%"
    }
  },
  previewSmsTextWrapper: {
    position: "absolute",
    bottom: 110,
    left: 0,
    right: 0,
    maxWidth: 200,
    margin: "0 auto"
  },
  previewSmsText: {
    background: theme.palette.primary.main,
    padding: "5px 7px",
    fontSize: 12.5,
    border: `1px solid ${theme.palette.primary.main}`,
    borderRadius: "7px 7px 0px 7px",
    maxHeight: 330,
    overflowY: "auto",
    color: theme.palette.primary.contrastText
  },
  previewSmsCredits: {
    position: "absolute",
    bottom: -5,
    left: 0,
    right: 0,
    width: "100%",
    textAlign: "center"
  },
  noRecipients: {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translateX(-50%) translateY(-50%)"
  },
  zIndexModal: {
    zIndex: theme.zIndex.modal + 5
  }
});

interface MessageEditViewProps {
  selection: string[];
  filteredCount: number;
  listEntity: string;
  templates?: EmailTemplate[];
  recipientsMessageData?: MessageData;
  emailFrom?: string;
  submitting?: boolean;
  opened?: boolean;
  listSearchQuery?: SearchQuery;
  values?: MessageExtended;
  classes?: any;
  selectionOnly?: boolean;
  getRecipientsMessageData?: (entityName: string, messageType: MessageType, listSearchQuery: SearchQuery, selection: string[], templateId: number) => void;
  clearOnClose?: AnyArgFunction;
  close?: AnyArgFunction;
  handleSubmit?: any;
  getMessageTemplates?: StringArgFunction;
  getEmailFrom?: NoArgFunction;
}

const initialValues: MessageExtended = {
  messageType: "Sms",
  selectAll: false,
  recipientsCount: 0,
  bindings: [],
  fromAddress: null,
  sendToStudents: true,
  sendToWithdrawnStudents: false,
  sendToActiveStudents: true,
  sendToTutors: true,
  sendToOtherContacts: true,
  sendToSuppressActiveStudents: false,
  sendToSuppressWithdrawnStudents: false,
  sendToSuppressStudents: false,
  sendToSuppressTutors: false,
  sendToSuppressOtherContacts: false
};

const bindingsRenderer: any = ({ fields }) => fields.map((i, n) => {
  const item: Binding = fields.get(n);

  const fieldProps: any = () => {
    switch (item.type) {
      case "Checkbox":
        return {
          color: "primary",
          stringValue: true
        };
      case "Text":
        return {
          listSpacing: false,
          multiline: true
        };
      case "Date":
        return {
          listSpacing: false,
          formatValue: YYYY_MM_DD_MINUSED
        };
      case "Money": {
        return {
          stringValue: true,
          listSpacing: false
        };
      }
      default: {
        return {
          listSpacing: false
        };
      }
    }
  };

  return (
    <Grid item xs={12} className="mb-2" key={item.label}>
      <Field
        name={`${i}.value`}
        label={`${item.label}`}
        type={item.type}
        component={DataTypeRenderer}
        validate={["Checkbox", "Text"].includes(item.type) ? null : validateSingleMandatoryField}
        {...fieldProps()}
      />
    </Grid>
  );
});

const labelsMap = label => {
  switch (label) {
    case "activeStudents":
      return "Active students";
    case "withdrawnStudents":
      return "Withdrawn students";
  }
  return label;
};

const Transition = React.forwardRef<unknown, TransitionProps>((props, ref) => (
  <Slide direction="up" ref={ref} {...props as any} mountOnEnter unmountOnExit />
));

const manualUrl = getManualLink("sending-messages");

const EntitiesToMessageTemplateEntitiesMap = {
  Invoice: ["Contact", "Invoice", "AbstractInvoice"],
  Application: ["Contact", "Application"],
  Contact: ["Contact"],
  Enrolment: ["Contact", "Enrolment"],
  CourseClass: ["Contact", "CourseClass", "Enrolment", "CourseClassTutor"],
  PaymentIn: ["Contact", "PaymentIn"],
  PaymentOut: ["Contact", "PaymentOut"],
  Payslip: ["Contact", "Payslip"],
  ProductItem: ["Contact", "Voucher", "Membership", "Article", "ProductItem"],
  WaitingList: ["Contact", "WaitingList"],
  Lead: ["Contact", "Lead"]
};

const getMessageTemplateEntities = entity => EntitiesToMessageTemplateEntitiesMap[entity] || [entity];

const SendMessageEditView = React.memo<MessageEditViewProps & DecoratedFormProps>(props => {
  const {
    classes,
    opened,
    getRecipientsMessageData,
    listSearchQuery,
    listEntity,
    values = {},
    selectionOnly,
    dispatch,
    form,
    templates,
    recipientsMessageData,
    emailFrom,
    selection,
    filteredCount,
    submitting,
    invalid,
    close,
    handleSubmit,
    getMessageTemplates,
    getEmailFrom,
    clearOnClose
  } = props;

  const htmlRef = useRef<HTMLDivElement>();

  const [preview, setPreview] = useState(null);
  const [suppressed, setSuppressed] = useState(false);

  const [selected, setSelected] = useState({
    withdrawnStudents: false,
    activeStudents: true,
    students: true,
    tutors: true,
    other: true
  });
  
  const checkAttachedShadow = () => {
    if (htmlRef.current && !htmlRef.current.shadowRoot) {
      htmlRef.current.attachShadow({ mode: 'open' });
    }
  };

  useEffect(() => {
    if (listEntity) {
      getMessageTemplates(listEntity);
      getEmailFrom();
      dispatch(initialize(form, { ...initialValues, entity: listEntity }));
    }
    return () => {
      clearOnClose();
    };
  }, [listEntity]);

  useEffect(() => {
    dispatch(change(form, "sendToWithdrawnStudents", selected.withdrawnStudents));
  }, [selected && selected.withdrawnStudents]);

  useEffect(() => {
    dispatch(change(form, "sendToActiveStudents", selected.activeStudents));
  }, [selected && selected.activeStudents]);

  useEffect(() => {
    dispatch(change(form, "sendToSuppressWithdrawnStudents", suppressed));
    dispatch(change(form, "sendToSuppressActiveStudents", suppressed));
    dispatch(change(form, "sendToSuppressStudents", suppressed));
    dispatch(change(form, "sendToSuppressTutors", suppressed));
    dispatch(change(form, "sendToSuppressOtherContacts", suppressed));
  }, [suppressed]);

  useEffect(() => {
    dispatch(change(form, "sendToStudents", selected.students));
  }, [selected && selected.students]);

  useEffect(() => {
    dispatch(change(form, "sendToTutors", selected.tutors));
  }, [selected && selected.tutors]);

  useEffect(() => {
    dispatch(change(form, "sendToOtherContacts", selected.other));
  }, [selected && selected.other]);

  useEffect(() => {
    if ( htmlRef.current) {
      checkAttachedShadow();
      if (values.messageType === "Email") {
        htmlRef.current.shadowRoot.innerHTML = preview;
      } else {
        htmlRef.current.shadowRoot.innerHTML = "";
      }
    }
  }, [values, preview, htmlRef.current?.shadowRoot]);
  
  const isEmailView = useMemo(() => values.messageType === "Email", [values.messageType]);

  const getTemplateById = useCallback(id => templates.find(t => t.id === id), [templates]);

  const getPreview = (val, selection, listSearchQuery) => {
    MessageService.getMessagePreview(
      val.recipientsCount,
      getMessageRequestModel(val, selection, listSearchQuery || {}),
      val.messageType
      )
      .then(setPreview)
      .catch(e => instantFetchErrorHandler(dispatch, e));
  };

  const getPreviewDebounced = useCallback<any>(debounce((val, selection, listSearchQuery) => {
    getPreview(val, selection, listSearchQuery);
  }, 600), []);

  useEffect(() => {
    if (opened && !invalid && values.bindings && values.recipientsCount && values.messageType) {
      getPreviewDebounced(values, selection, listSearchQuery);
    }
  }, [values, selection, listSearchQuery, invalid, opened]);

  const onTemplateChange = (e, value, previousValue) => {
    if (value && value !== previousValue) {
      const selectedTemplate = getTemplateById(value);
      if (selectedTemplate) {
        setPreview(null);

        // set variables with default empty values
        dispatch(change(form, "bindings", selectedTemplate.variables.map(v =>
          ({ ...v, value: v.type === "Checkbox" ? false : v.type === "Text" ? "" : v.value }))));

        if (htmlRef.current) {
          checkAttachedShadow();
          htmlRef.current.shadowRoot.innerHTML = "";
        }

        if (selectedTemplate.type === "Email") {
          dispatch(change(form, "messageType", "Email"));
          dispatch(change(form, "fromAddress", emailFrom));
        } else {
          dispatch(change(form, "fromAddress", null));
        }
      }

      if (selectedTemplate.type === "Sms") {
        dispatch(change(form, "messageType", "Sms"));
      }

      setSuppressed(true);

      getRecipientsMessageData(listEntity, selectedTemplate.type, listSearchQuery, values.selectAll ? null : selection, selectedTemplate.id);
    }
  };

  useEffect(() => {
    if (opened && templates && templates.length) {
      dispatch(change(form, "templateId", templates[0].id));
      onTemplateChange(null, templates[0].id, null);
    }
  }, [templates, opened]);

  const setSelectAll = useCallback((v: boolean) => {
    dispatch(change(form, "selectAll", v));
    getRecipientsMessageData(listEntity, values.messageType, listSearchQuery, v ? null : selection, values.templateId);
  }, [form, values]);

  const totalCounter = useMemo<Recipients>(() => {
    const counter: Recipients = {
      activeStudents: null,
      withdrawnStudents: null,
      students: null,
      tutors: null,
      other: null
    };

    if (!recipientsMessageData) {
      return counter;
    }

    const selectionPath = values.selectAll ? "filtered" : "selected";
    const countersPath: Recipients = recipientsMessageData[selectionPath] && recipientsMessageData[selectionPath][values.messageType];

    if (!countersPath) {
      return counter;
    }

    Object.keys(countersPath).forEach(recipientsName => {
      counter[recipientsName] = countersPath[recipientsName];
    });

    return counter;
  }, [recipientsMessageData, values.messageType, values.selectAll]);

  const openLink = ids => {
    const aql = `id in (${ids.toString()})`;
    let url = `/contact?search=${aql}`;
    if (url.length >= 2048) {
      const id = `f${(+new Date).toString(16)}`;
      saveCategoryAQLLink({ AQL: aql, id, action: "add" });
      url = `/contact?customSearch=${id}`;
    }

    setTimeout(() => {
      openInternalLink(url);
    }, 400);
  };

  const counterItems = useMemo(() => Object.keys(totalCounter).map(recipientsName => {
    if (!Object.keys(totalCounter[recipientsName] || {}).some(k => totalCounter[recipientsName][k]?.length)) {
      return null;
    }

    const totalHeaderCount = suppressed
      ? (totalCounter[recipientsName].sendIds?.length || 0) + (totalCounter[recipientsName].suppressToSendIds?.length || 0)
      : totalCounter[recipientsName].sendIds?.length || 0;

    const headerIds = suppressed
      ? Array.from(new Set([...totalCounter[recipientsName].sendIds, ...totalCounter[recipientsName].suppressToSendIds]))
      : totalCounter[recipientsName].sendIds;

    return (
      <Fragment key={recipientsName}>
        <div className="centeredFlex">
          <Typography variant="body2" className="heading">
            {`${totalHeaderCount} ${labelsMap(recipientsName)}`}
          </Typography>
          <IconButton size="small" color="primary" onClick={() => openLink(headerIds)}>
            <OpenInNew fontSize="inherit" />
          </IconButton>
          <Switch onChange={(e, v) => setSelected(prev => ({ ...prev, [recipientsName]: v }))} checked={selected[recipientsName]} />
        </div>
        {selected[recipientsName] ? (
          <>
            {totalCounter[recipientsName]?.withoutDestinationIds?.length > 0 && (
              <Typography variant="body2">
                {`Skipping ${totalCounter[recipientsName].withoutDestinationIds?.length || 0} without ${
                  isEmailView ? "email or with undeliverable email" : "mobile phone or with undeliverable mobile phone"}`}
                <IconButton size="small" color="primary" onClick={() => openLink(totalCounter[recipientsName].withoutDestinationIds)}>
                  <OpenInNew fontSize="inherit" />
                </IconButton>
              </Typography>
            )}
            {!suppressed && totalCounter[recipientsName].suppressToSendIds?.length !== 0 && (
              <Typography variant="body2">
                {`Skipping ${totalCounter[recipientsName].suppressToSendIds?.length || 0} not accepting marketing material`}
                <IconButton size="small" color="primary" onClick={() => openLink(totalCounter[recipientsName].suppressToSendIds)}>
                  <OpenInNew fontSize="inherit" />
                </IconButton>
              </Typography>
            )}
          </>
        ) : null}
      </Fragment>
    );
  }), [totalCounter, suppressed, selected]);

  useEffect(() => {
    let recipientsCount = 0;

    Object.keys(totalCounter).forEach(k => {
      if (totalCounter[k] && selected[k]) {
        recipientsCount += totalCounter[k]?.sendIds?.length || 0;

        if (suppressed) {
          recipientsCount += totalCounter[k]?.suppressToSendIds?.length || 0;
        }
      }
    });

    dispatch(change(form, "recipientsCount", recipientsCount));
  }, [totalCounter, suppressed, selected]);

  const textSmsCreditsCount = !isEmailView && preview && Math.ceil(preview.length / 160);

  const filteredTemplatesByVaribleCount = useMemo<EmailTemplate[]>(() =>
    templates?.filter(template => template?.variables.filter(variable => variable.type === DataType.Object).length === 0) || [], [templates]);

  const onSubmit = model => dispatch(sendMessage(model, selection));

  return (
    <Dialog
      fullScreen
      open={opened}
      TransitionComponent={Transition}
      disableEnforceFocus
      classes={{
        root: classes.zIndexModal,
        paper: "overflow-hidden"
      }}
    >
      <LoadingIndicator position="fixed" />
      <form onSubmit={handleSubmit(onSubmit)} autoComplete="off" noValidate>
        <AppBarContainer
          disabledScrolling
          disableInteraction
          noDrawer
          manualUrl={manualUrl}
          onCloseClick={close}
          submitButtonText="Send"
          title={(
            <div>
              Send
              {' '}
              { isEmailView ? "email" : "SMS" }
            </div>
          )}
        >
          <div className="appBarContainer">
            <Grid container columnSpacing={3} spacing={3}>
              <Grid item xs={12} md={6}>
                <Grid item xs className="centeredFlex mb-2">
                  <RecipientsSelectionSwitcher
                    selectedRecords={selection.length}
                    allRecords={filteredCount}
                    selectAll={values.selectAll}
                    setSelectAll={setSelectAll}
                    disabled={submitting /* count === null */}
                    selectionOnly={selectionOnly}
                  />
                </Grid>

                <FormField
                  type="select"
                  name="templateId"
                  label="Template"
                  selectValueMark="id"
                  selectLabelMark="name"
                  categoryKey="entity"
                  items={filteredTemplatesByVaribleCount}
                  onChange={onTemplateChange}
                  className="mb-2"
                  required
                  sort
                />

                <FieldArray name="bindings" component={bindingsRenderer} rerenderOnEveryChange />

                {isEmailView && (
                  <FormField type="text" name="fromAddress" label="From address" className="mb-2" />
                )}

                <FormControlLabel
                  className="mb-2"
                  control={(
                    <StyledCheckbox
                      checked={!suppressed}
                      onChange={() => {
                        setSuppressed(!suppressed);
                      }}
                      color="primary"
                    />
                  )}
                  label="This is a marketing message"
                />
                <br />
                {counterItems}
              </Grid>

              <Grid item xs={12} md={6} className="relative">
                <Typography variant="body1" className={clsx(classes.noRecipients, { "d-none": values.recipientsCount })}>
                  No recipients
                </Typography>
                <div className={clsx({ "d-none": !values.recipientsCount })}>
                  <div className={isEmailView ? undefined : "d-none"}>
                    <Typography variant="caption" color="textSecondary">
                      Preview
                    </Typography>
                    <Card>
                      <CardContent>
                        <div className={clsx("overflow-auto", classes.previewContent)} ref={htmlRef} />
                      </CardContent>
                    </Card>
                  </div>
                  <div className={clsx("relative w-100", isEmailView && "d-none", classes.previewSmsWrapper)}>
                    <div className={classes.previewSmsImage}>
                      <img src={previewSmsImage} alt="preview-sms" />
                    </div>
                    {preview && String(preview).length > 0 && (
                      <>
                        <div className={clsx("text-pre-wrap", classes.previewSmsTextWrapper)}>
                          <div className={classes.previewSmsText}>
                            {preview}
                          </div>
                        </div>
                        {textSmsCreditsCount > 1 && (
                          <Typography variant="caption" color="textSecondary" className={classes.previewSmsCredits}>
                            {`This message requires ${textSmsCreditsCount} credits to send.`}
                          </Typography>
                        )}
                      </>
                    )}
                  </div>
                </div>
              </Grid>
            </Grid>
          </div>
        </AppBarContainer>
      </form>
    </Dialog>
  );
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(SEND_MESSAGE_FORM_NAME)(state),
  emailFrom: state.userPreferences[EMAIL_FROM_KEY],
  submitting: state.fetch.pending,
  opened: state.sendMessage.open,
  templates: state.list.emailTemplatesWithKeyCode,
  recipientsMessageData: state.list.recepients
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  getRecipientsMessageData: (entityName: string, messageType: MessageType, listSearchQuery: SearchQuery, selection: string[], templateId: number) => dispatch(
    getRecipientsMessageData(
      entityName,
      messageType,
      listSearchQuery,
      selection,
      templateId
    )
  ),
  clearOnClose: () => {
    dispatch(clearRecipientsMessageData());
  },
  close: () => {
    dispatch(closeSendMessage());
    dispatch(clearRecipientsMessageData());
  },
  getMessageTemplates: (entitity: string) => dispatch(getEmailTemplatesWithKeyCode(getMessageTemplateEntities(entitity))),
  getEmailFrom: () => dispatch(getUserPreferences([EMAIL_FROM_KEY])),
});

export default reduxForm<any, any, any>({
  form: SEND_MESSAGE_FORM_NAME,
})(connect(mapStateToProps, mapDispatchToProps)(withStyles(SendMessageEditView, styles)));
