import Dialog from "@mui/material/Dialog";
import React from "react";
import MergeContacts from "./merge-contacts/MergeContacts";

const MergeContactsModal: React.FC<any> = props => {
  const { opened, onClose } = props;
  return (
    <Dialog
      fullScreen
      open={opened}
      onClose={onClose}
      classes={{
        paperFullScreen: "defaultBackgroundColor"
      }}
    >
      <div className="w-100 relative overflow-y-auto">
        <MergeContacts mergeCloseOnSuccess={onClose} isModal />
      </div>
    </Dialog>
  );
};

export default MergeContactsModal;
