/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
// eslint-disable-next-line import/no-extraneous-dependencies
import React, {
  useCallback, useEffect, useMemo, useState, Fragment, useRef
} from "react";
import { Dispatch } from "redux";
import {
 change, Field, FieldArray, initialize
} from "redux-form";
import { connect } from "react-redux";
import debounce from "lodash.debounce";
import clsx from "clsx";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Button from "@mui/material/Button";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import IconButton from "@mui/material/IconButton";
import OpenInNew from "@mui/icons-material/OpenInNew";
import {
  Binding, DataType,
  EmailTemplate, MessageType, Recipients, /* Recipients, */ SearchQuery
} from "@api/model";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import DataTypeRenderer from "../../../../common/components/form/DataTypeRenderer";
import FormField from "../../../../common/components/form/formFields/FormField";
import {
  clearListNestedEditRecord,
  clearRecipientsMessageData,
  closeListNestedEditRecord,
  getRecipientsMessageData
} from "../../../../common/components/list-view/actions";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { EMAIL_FROM_KEY } from "../../../../constants/Config";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { EditViewProps } from "../../../../model/common/ListView";
import { MessageData, MessageExtended } from "../../../../model/common/Message";
import { State } from "../../../../reducers/state";
import MessageService from "../services/MessageService";
import RecipientsSelectionSwitcher from "./RecipientsSelectionSwitcher";
import { Switch } from "../../../../common/components/form/formFields/Switch";
import { StyledCheckbox } from "../../../../common/components/form/formFields/CheckboxField";
import previewSmsImage from "../../../../../images/preview-sms.png";
import { validateSingleMandatoryField } from "../../../../common/utils/validation";
import { getMessageRequestModel } from "../utils";
import { openInternalLink, saveCategoryAQLLink } from "../../../../common/utils/links";
import { getByType } from "../../../automation/containers/integrations/utils";
import IntegrationImages from "../../../automation/containers/integrations/IntegrationImages";
import IntegrationDescription from "../../../automation/containers/integrations/components/IntegrationDescription";
import AppBarContainer from "../../../../common/components/layout/AppBarContainer";

const styles = theme => createStyles({
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
  }
});

interface MessageEditViewProps extends EditViewProps {
  values: MessageExtended;
  selection?: string[];
  classes?: any;
  templates: EmailTemplate[];
  recipientsMessageData?: MessageData;
  emailFrom?: string;
  filteredCount?: number;
  submitting?: boolean;
  listSearchQuery?: SearchQuery;
  listEntity?: string;
  getRecipientsMessageData?: (entityName: string, messageType: MessageType, listSearchQuery: SearchQuery, selection: string[], templateId: number) => void;
  clearOnClose?: AnyArgFunction;
  close?: AnyArgFunction;
}

const initialValues: MessageExtended = {
  messageType: "Sms",
  selectAll: false,
  recipientsCount: 0,
  bindings: [],
  sendToStudents: true,
  sendToTutors: true,
  sendToOtherContacts: true,
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

const SendMessageEditView = React.memo<MessageEditViewProps>(props => {
  const {
    classes,
    getRecipientsMessageData,
    listSearchQuery,
    listEntity,
    manualLink,
    values,
    dispatch,
    form,
    templates,
    recipientsMessageData,
    emailFrom,
    selection,
    filteredCount,
    submitting,
    invalid,
    isNew,
    dirty,
    close
  } = props;

  const htmlRef = useRef<HTMLDivElement>();

  useEffect(() => {
    if (htmlRef.current && !htmlRef.current.shadowRoot) {
      htmlRef.current.attachShadow({ mode: 'open' });
    }
  }, [htmlRef.current]);

  const [preview, setPreview] = useState(null);
  const [isMarketing, setIsMarketing] = useState(true);

  const [suppressed, setSuppressed] = useState(false);

  const [selected, setSelected] = useState({
    withdrawnStudents: false,
    activeStudents: true,
    students: true,
    tutors: true,
    other: true
  });

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

  const isEmailView = useMemo(() => values.messageType === "Email", [values.messageType]);

  const getTemplateById = useCallback(id => templates.find(t => t.id === id), [templates]);

  const getPreview = val => {
    MessageService.getMessagePreview(
      val.recipientsCount,
      getMessageRequestModel(val, selection, listSearchQuery),
      val.messageType
      )
      .then(r => {
        setPreview(r);
          if (htmlRef.current) {
            if (val.messageType === "Email") {
              htmlRef.current.shadowRoot.innerHTML = r;
            } else {
              htmlRef.current.shadowRoot.innerHTML = "";
            }
          }
      })
      .catch(e => instantFetchErrorHandler(dispatch, e));
  };

  const getPreviewDebounced = useCallback<any>(debounce(val => {
    getPreview(val);
  }, 600), []);

  useEffect(() => {
    if (!invalid && values.bindings && values.recipientsCount && values.messageType) {
      getPreviewDebounced(values);
    }
  }, [values]);

  const onTemplateChange = (e, value, previousValue) => {
    if (value && value !== previousValue) {
      const selectedTemplate = getTemplateById(value);
      if (selectedTemplate) {
        setPreview(null);

        // set variables with default empty values
        dispatch(change(form, "bindings", selectedTemplate.variables.map(v =>
          ({ ...v, value: v.type === "Checkbox" ? false : v.type === "Text" ? "" : v.value }))));

        if (htmlRef.current && htmlRef.current.shadowRoot) {
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
      getRecipientsMessageData(listEntity, selectedTemplate.type, listSearchQuery, values.selectAll ? null : selection, selectedTemplate.id);
    }
  };

  useEffect(() => {
    if (templates && templates.length) {
      dispatch(change(form, "templateId", templates[0].id));
      onTemplateChange(null, templates[0].id, null);
    }
  }, [templates]);

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
          <IconButton size="small" color="secondary" onClick={() => openLink(headerIds)}>
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
                <IconButton size="small" color="secondary" onClick={() => openLink(totalCounter[recipientsName].withoutDestinationIds)}>
                  <OpenInNew fontSize="inherit" />
                </IconButton>
              </Typography>
            )}
            {isMarketing && totalCounter[recipientsName].suppressToSendIds?.length !== 0 && (
              <Typography variant="body2">
                {`Skipping ${totalCounter[recipientsName].suppressToSendIds?.length || 0} not accepting marketing material`}
                <IconButton size="small" color="secondary" onClick={() => openLink(totalCounter[recipientsName].suppressToSendIds)}>
                  <OpenInNew fontSize="inherit" />
                </IconButton>
              </Typography>
            )}
          </>
        ) : null}
      </Fragment>
    );
  }), [isMarketing, totalCounter, suppressed, selected]);

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

  const filterTemplatesByVaribleCount = (): EmailTemplate[] =>
    templates.filter(template => template.variables.filter(variable => variable.type === DataType.Object).length === 0);

  return (
    <div className="appBarContainer">
      <CustomAppBar>
        <div className="centeredFlex w-100">
          <div className="flex-fill">
            <Typography className="appHeaderFontSize" color="inherit">
              Send
              {' '}
              { isEmailView ? "email" : "SMS" }
            </Typography>
          </div>
          <div>
            {manualLink && (
              <AppBarHelpMenu
                manualUrl={manualLink}
              />
            )}

            <Button onClick={close} className="closeAppBarButton">
              Close
            </Button>
            <Button
              type="submit"
              classes={{
                root: "whiteAppBarButton",
                disabled: "whiteAppBarButtonDisabled"
              }}
              disabled={invalid || (!isNew && !dirty) || !values.recipientsCount}
            >
              Send
            </Button>
          </div>
        </div>
      </CustomAppBar>

      <div className="p-3">
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Grid item xs className="centeredFlex mb-2">
              <RecipientsSelectionSwitcher
                selectedRecords={selection.length}
                allRecords={filteredCount}
                selectAll={values.selectAll}
                setSelectAll={setSelectAll}
                disabled={submitting /* count === null */}
              />
            </Grid>

            <FormField
              type="select"
              name="templateId"
              label="Template"
              selectValueMark="id"
              selectLabelMark="name"
              categoryKey="entity"
              items={filterTemplatesByVaribleCount() || []}
              onChange={onTemplateChange}
              required
            />

            <FieldArray name="bindings" component={bindingsRenderer} rerenderOnEveryChange />

            {isEmailView && (
              <FormField type="text" name="fromAddress" label="From address" />
            )}

            <FormControlLabel
              className="mb-2"
              control={(
                <StyledCheckbox
                  checked={isMarketing}
                  onChange={() => {
                    setIsMarketing(!isMarketing);
                    setSuppressed(!suppressed);
                  }}
                  color="secondary"
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
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  templates: state.list.emailTemplatesWithKeyCode,
  recipientsMessageData: state.list.recepients,
  emailFrom: state.userPreferences[EMAIL_FROM_KEY],
  selection: state.list.selection,
  listSearchQuery: state.list.searchQuery,
  filteredCount: state.list.records.filteredCount,
  submitting: state.fetch.pending,
  listEntity: state.list.records.entity
});

const mapDispatchToProps = (dispatch: Dispatch, ownProps) => ({
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
    dispatch(clearListNestedEditRecord(ownProps.nestedIndex));
  },
  close: () => {
    dispatch(closeListNestedEditRecord(ownProps.nestedIndex));
  }
});

const SendMessageEditViewResolver: React.FC<MessageEditViewProps> = props => {
  const {
    dispatch, form, clearOnClose, values, listEntity
  } = props;

  useEffect(() => {
    dispatch(initialize(form, { ...initialValues, entity: listEntity }));
    return () => {
      clearOnClose();
    };
  }, []);

  return values ? <SendMessageEditView {...props} /> : null;
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(SendMessageEditViewResolver));
