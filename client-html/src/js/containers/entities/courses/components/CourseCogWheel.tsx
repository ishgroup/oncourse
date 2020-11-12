import MenuItem from "@material-ui/core/MenuItem";
import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";
import { duplicateCourses } from "../actions";

const CourseCogWheel = React.memo<any>(props => {
  const {
 selection, menuItemClass, closeMenu, duplicate
} = props;
  const selectedAndNotNew = React.useMemo(() => selection.length >= 1 && selection[0] !== "NEW", [selection]);

  const onClick = React.useCallback(e => {
    const status = e.target.getAttribute("datatype");

    switch (status) {
      case "Duplicate":
        duplicate(selection);
        break;
      default:
        break;
    }

    closeMenu();
  }, []);

  return (
    <>
      <MenuItem disabled={!selectedAndNotNew} className={menuItemClass} onClick={onClick} datatype="Duplicate">
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
