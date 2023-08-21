import MenuItem from "@mui/material/MenuItem";
import React, { useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";
import { duplicateCourses } from "../actions";
import RelationshipView from "./RelationshipView";

const CourseCogWheel = React.memo<any>(props => {
  const {
    selection, menuItemClass, closeMenu, duplicate
  } = props;
  const [dialogOpened, setDialogOpened] = useState(null);
  const selectedAndNotNew = React.useMemo(() => selection.length >= 1 && selection[0] !== "NEW", [selection]);

  const onClick = React.useCallback(e => {
    const status = e.target.getAttribute("role");

    switch (status) {
      case "Duplicate":
        duplicate(selection);
        closeMenu();
        break;
      case "RelationshipView":
        break;
      default:
        break;
    }

    setDialogOpened(status);
  }, []);

  return (
    <>
      <RelationshipView
        open={dialogOpened === "RelationshipView"}
        selection={selection}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
      />

      <MenuItem disabled={!selectedAndNotNew} className={menuItemClass} onClick={onClick} role="RelationshipView">
        Relationship view
      </MenuItem>
      <MenuItem disabled={!selectedAndNotNew} className={menuItemClass} onClick={onClick} role="Duplicate">
        Duplicate
        {' '}
        {selection.length}
        {' '}
        cours
        {selection.length <= 1 ? "e" : "es"}
      </MenuItem>
      <BulkEditCogwheelOption {...props} />
    </>
  );
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  duplicate: (ids: number[]) => dispatch(duplicateCourses(ids))
});

export default connect<any, any, any>(null, mapDispatchToProps)(CourseCogWheel);