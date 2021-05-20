import MenuItem from "@material-ui/core/MenuItem";
import React, { useCallback, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { sendMessage } from "../../../../../../containers/entities/messages/actions";
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

const SendMessageMenu: React.FC<SendMessageMenuProps> = props => {
  const {
    selection, entity, openSendMessageEditView, closeAll
  } = props;

  const isNew = useMemo(() => Array.isArray(selection) && selection.length >= 1 && selection[0].toLowerCase() === "new", [selection]);

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
  openSendMessageEditView: (record: any) => dispatch(
    setListNestedEditRecord("SendMessage", record, model => dispatch(sendMessage(model)), true)
  )
});

export default connect<any, any, any>(null, mapDispatchToProps)(SendMessageMenu);
