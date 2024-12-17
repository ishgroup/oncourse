import MenuItem from "@mui/material/MenuItem";
import React, { useCallback, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { openSendMessage } from "../../../../../actions";
import { IAction } from '../../../../../actions/IshAction';

interface SendMessageMenuProps {
  selection: string[];
  entity: string;
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

const mapDispatchToProps = (dispatch: Dispatch<IAction>) => ({
  openSendMessageEditView: () => dispatch(
    openSendMessage()
  )
});

export default connect<any, any, any>(null, mapDispatchToProps)(SendMessageMenu);
