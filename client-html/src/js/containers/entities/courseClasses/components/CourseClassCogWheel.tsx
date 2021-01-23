/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import MenuItem from "@material-ui/core/MenuItem";
import React, {
 memo, useCallback, useEffect, useMemo, useState
} from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import CreateCertificateMenu from "../../../../common/components/list-view/components/bottom-app-bar/components/CreateCertificateMenu";
import EntityService from "../../../../common/services/EntityService";
import { State } from "../../../../reducers/state";
import AvetmissExportModal from "../../../avetmiss-export/components/modal/AvetmissExportModal";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";
import PayslipGenerateCogwheelAction from "../../payslips/components/PayslipGenerateCogwheelAction";
import CancelCourseClassModal from "./cancel/CancelCourseClassModal";
import DuplicateCourseClassModal from "./duplicate-courseClass/DuplicateCourseClassModal";
import DuplicateTraineeshipModal from "./duplicate-courseClass/DuplicateTraineeshipModal";
import {getCommonPlainRecords, setCommonPlainSearch} from "../../../../common/actions/CommonPlainRecordsActions";

const CourseClassCogWheel = memo<any>(props => {
  const {
    selection,
    menuItemClass,
    closeMenu,
    editRecord,
    dispatch,
    data,
    showConfirm,
    setCourseClassPlainRequest,
    getCourseClassPlainRecord
  } = props;
  const [dialogOpened, setDialogOpened] = useState(null);
  const [isTraneeship, setIsTraneeship] = useState(false);
  const [selectedClassesEnrolmentsCount, setSelectedClassesEnrolmentsCount] = useState(0);

  const selectedAndNotNew = useMemo(() => selection.length >= 1 && selection[0] !== "NEW", [selection]);

  useEffect(
    () => {
      setSelectedClassesEnrolmentsCount(0);
    },
    []
  );

  useEffect(() => {
    if (selection.length === 1) {
      setCourseClassPlainRequest(selection[0]);
      getCourseClassPlainRecord();
    }
  }, []);

  useEffect(
    () => {
      if (selection.length) {
        EntityService.getPlainRecords("CourseClass", "course.isTraineeship", `id in (${selection})`)
          .then(res => {
            setIsTraneeship(res.rows.filter(value => JSON.parse(value.values[0]) === true).length !== 0);
          });
      }
    },
    [selection]
  );

  const isCancelable = (id: number): boolean => {
    const { columns, rows } = data;
    const filteredColumns = columns.filter(c => c.visible || c.system);
    const rowValueIndex = filteredColumns.findIndex( c => c.attribute === "isCancelled");
    const rowValues = rows.find(row => row.id === id).values;

    return !(rowValues[rowValueIndex] === "true");
  };

  const isCancelEnable = useMemo(() => selection.length === 1 && selection[0] !== "NEW" && isCancelable(selection[0]),
    [selection, editRecord, data]);

  const onClick = useCallback(
    e => {
      const status = e.target.getAttribute("role");

      if (status === "Avetmiss-Export") {
        EntityService.getPlainRecords(
          "Enrolment",
          "outcomes.id",
          `courseClass.id in (${selection.toString()}) and outcomes.id not is null`
        ).then(res => setSelectedClassesEnrolmentsCount(res.rows.length));
      }

      setDialogOpened(status);
    },
    [selection]
  );

  const classesCountLabel = useMemo(() => `${selection.length} class${selection.length <= 1 ? "" : "es"}`, [
    selection.length
  ]);

  return (
    <>
      <DuplicateCourseClassModal
        selection={selection}
        opened={dialogOpened === "Duplicate"}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
      />

      <DuplicateTraineeshipModal
        selection={selection}
        opened={dialogOpened === "DuplicateTraineeship"}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
      />
      <CancelCourseClassModal
        selection={selection}
        opened={dialogOpened === "Cancel"}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
      />
      <AvetmissExportModal
        entity="CourseClass"
        selection={selection}
        opened={dialogOpened === "Avetmiss-Export"}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
        enrolmentsCount={selectedClassesEnrolmentsCount}
      />
      <CreateCertificateMenu
        entity="CourseClass"
        disableMenu={!selectedAndNotNew}
        selection={selection}
        closeMenu={closeMenu}
        dispatch={dispatch}
      />
      {!isTraneeship && (
        <MenuItem disabled={!selectedAndNotNew} className={menuItemClass} role="Duplicate" onClick={onClick}>
          Duplicate
          {' '}
          {classesCountLabel}
        </MenuItem>
      )}

      {isTraneeship && selection.length === 1 && (
        <MenuItem className={menuItemClass} role="DuplicateTraineeship" onClick={onClick}>
          Duplicate Traineeship and enrol
        </MenuItem>
      )}

      <MenuItem disabled={!isCancelEnable} className={menuItemClass} role="Cancel" onClick={onClick}>
        Cancel class
      </MenuItem>
      <PayslipGenerateCogwheelAction
        entity="CourseClass"
        generateLabel={`Generate tutor pay${
          selection.length ? ` for ${selection.length} class${selection.length <= 1 ? "" : "s"}` : ""
        }`}
        closeMenu={closeMenu}
        showConfirm={showConfirm}
        menuItemClass={menuItemClass}
        selection={selection}
      />
      <MenuItem disabled={!selectedAndNotNew} className={menuItemClass} role="Avetmiss-Export" onClick={onClick}>
        AVETMISS 8 export
      </MenuItem>
      <BulkEditCogwheelOption {...props} />
    </>
  );
});

const mapStateToProps = (state: State) => ({
  search: state.list.searchQuery,
  data: state.list.records,
  editRecord: state.list.editRecord
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setCourseClassPlainRequest: id => dispatch(setCommonPlainSearch("CourseClass", `id is ${id}`)),
  getCourseClassPlainRecord: () => dispatch(
    getCommonPlainRecords("CourseClass", 0, "cancelWarningMessage,course.name,course.code,code,validEnrolmentCount")
  )
});

export default connect(mapStateToProps, mapDispatchToProps)(CourseClassCogWheel);
