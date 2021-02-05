import MenuItem from "@material-ui/core/MenuItem";
import React, { useCallback, useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { EMAIL_FROM_KEY } from "../../../../../../constants/Config";
import { sendMessage } from "../../../../../../containers/entities/messages/actions";
import { getEmailTemplatesWithKeyCode, getUserPreferences } from "../../../../../actions";
import { setListNestedEditRecord } from "../../../actions";

interface SendMessageMenuProps {
  classes: any;
  selection: string[];
  entity: string;
  getMessageTemplates: any;
  getEmailFrom: () => void;
  openSendMessageEditView: any;
  closeAll: () => void;
}

const EntitiesToMessageTemplateEntitiesMap = {
  Invoice: ["Contact", "Invoice"],
  Application: ["Contact", "Application"],
  Contact: ["Contact"],
  Enrolment: ["Contact", "Enrolment"],
  CourseClass: ["Contact", "CourseClass", "Enrolment"],
  PaymentIn: ["Contact", "PaymentIn"],
  PaymentOut: ["Contact", "PaymentOut"],
  Payslip: ["Contact", "Payslip"],
  ProductItem: ["Contact", "Voucher", "Membership", "Article", "ProductItem"],
  WaitingList: ["Contact", "WaitingList"]
};

const getMessageTemplateEntities = entity => EntitiesToMessageTemplateEntitiesMap[entity] || [entity];

const SendMessageMenu: React.FC<SendMessageMenuProps> = props => {
  const {
    selection, entity, getMessageTemplates, getEmailFrom, openSendMessageEditView, closeAll
  } = props;

  const isNew = useMemo(() => Array.isArray(selection) && selection.length >= 1 && selection[0].toLowerCase() === "new", [selection]);

  useEffect(() => {
    if (!entity) return;
    getMessageTemplates(getMessageTemplateEntities(entity));
    getEmailFrom();
  }, [entity]);

  const onSendMessage = useCallback(() => {
    openSendMessageEditView({ selection });
    closeAll();
  }, [selection, entity]);

  return (
    <MenuItem disabled={isNew} className="listItemPadding" onClick={onSendMessage}>
      Send message
    </MenuItem>
  );
};


const mapDispatchToProps = (dispatch: Dispatch) => ({
  getMessageTemplates: (entities: string[]) => dispatch(getEmailTemplatesWithKeyCode(entities)),
  getEmailFrom: () => dispatch(getUserPreferences([EMAIL_FROM_KEY])),
  openSendMessageEditView: (record: any) => dispatch(
    setListNestedEditRecord("SendMessage", record, model => dispatch(sendMessage(model)), true)
  )
});

export default connect<any, any, any>(null, mapDispatchToProps)(SendMessageMenu);
