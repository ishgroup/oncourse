import MenuItem from "@mui/material/MenuItem";
import Tooltip from "@mui/material/Tooltip";
import React, { useCallback, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { openSendMessage } from "../../../../../actions";

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
    <Tooltip title="Service is temporarily unavailable. We are already working on a solution to enable it as soon as possible">
      <div>
        <MenuItem disabled className="listItemPadding" onClick={onSendMessage}>
          Send message
        </MenuItem>
      </div>
    </Tooltip>
  );
};

const mapDispatchToProps = (dispatch: Dispatch) => ({
  openSendMessageEditView: () => dispatch(
    openSendMessage()
  )
});

export default connect<any, any, any>(null, mapDispatchToProps)(SendMessageMenu);
