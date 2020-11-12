import { EmailTemplate } from "@api/model";
import MenuItem from "@material-ui/core/MenuItem";
import React, { useCallback, useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { EMAIL_FROM_KEY } from "../../../../../../constants/Config";
import { sendMessage } from "../../../../../../containers/entities/messages/actions";
import { State } from "../../../../../../reducers/state";
import { getEmailTemplatesWithKeyCode, getUserPreferences } from "../../../../../actions";
import { setListNestedEditRecord } from "../../../actions";

interface SendMessageMenuProps {
  classes: any;
  selection: string[];
  entity: string;
  messageTemplates: EmailTemplate[];
  getMessageTemplates: any;
  getEmailFrom: () => void;
  openSendMessageEditView: any;
  closeAll: () => void;
}

const SendMessageEntities = [
  "Invoice",
  "Application",
  "Contact",
  "Enrolment",
  "CourseClass",
  "PaymentIn",
  "PaymentOut",
  "ProductItem",
  "WaitingList"
];

const EntitiesToMessageTemplateEntitiesMap = {
  Invoice: ["Contact", "Invoice"],
  Application: ["Contact", "Application"],
  Contact: ["Contact"],
  Enrolment: ["Contact", "Enrolment"],
  CourseClass: ["Contact", "CourseClass", "Enrolment"],
  PaymentIn: ["Contact", "PaymentIn"],
  PaymentOut: ["Contact", "PaymentOut"],
  ProductItem: ["Contact", "Voucher", "Membership", "Article", "ProductItem"],
  WaitingList: ["Contact", "WaitingList"]
};

const getMessageTemplateEntities = entity => EntitiesToMessageTemplateEntitiesMap[entity] || [entity];

const SendMessageMenu: React.FC<SendMessageMenuProps> = props => {
  const {
    selection, entity, messageTemplates, getMessageTemplates, getEmailFrom, openSendMessageEditView, closeAll
  } = props;

  const isNew = useMemo(() => Array.isArray(selection) && selection.length >= 1 && selection[0].toLowerCase() === "new", [selection]);
  const isSendMessageAvailable = useMemo(
    () => SendMessageEntities.includes(entity) && Array.isArray(messageTemplates) && messageTemplates.length, [entity, messageTemplates]
  );

  useEffect(() => {
    if (!entity) return;
    getMessageTemplates(getMessageTemplateEntities(entity));
    getEmailFrom();
  }, [entity]);

  const onSendMessage = useCallback(() => {
    openSendMessageEditView({ selection });
    closeAll();
  }, [selection, entity]);

  return isSendMessageAvailable ? (
    <MenuItem disabled={isNew} className="listItemPadding" onClick={onSendMessage}>
      Send message
    </MenuItem>
  ) : null;
};

const mapStateToProps = (state: State) => ({
  messageTemplates: state.list.emailTemplatesWithKeyCode,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  getMessageTemplates: (entities: string[]) => dispatch(getEmailTemplatesWithKeyCode(entities)),
  getEmailFrom: () => dispatch(getUserPreferences([EMAIL_FROM_KEY])),
  openSendMessageEditView: (record: any) => dispatch(
    setListNestedEditRecord("SendMessage", record, model => dispatch(sendMessage(model)), true)
  )
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(SendMessageMenu);