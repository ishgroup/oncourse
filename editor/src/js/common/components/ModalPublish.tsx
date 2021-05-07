import React, {useState} from 'react';
import {createStyles, makeStyles, Modal} from '@material-ui/core';
import CustomButton from "./CustomButton";
import EditInPlaceField from "./form/form-fields/EditInPlaceField";
import {stubFunction} from "../utils/Components";
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {publish} from "../../containers/history/actions";
import {VersionStatus} from "../../../../build/generated-sources/api";
import {State} from "../../reducers/state";

const useStyles = makeStyles(theme =>
  createStyles({
    paper: {
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      position: 'absolute',
      boxShadow: theme.shadows[5],
      padding: theme.spacing(2, 4, 3),
    },
    modalTitle: {
      fontSize: "20px",
      fontFamily: "Inter, sans-serif",
      fontWeight: 600,
      paddingBottom: theme.spacing(2),
      color: theme.palette.text.primary,
      textAlign: "left",
    },
    modalContent: {
      position: "relative",
      display: "flex",
      flexDirection: "column",
      backgroundColor: "#fff",
      backgroundClip: "padding-box",
      border: "1px solid rgba(0, 0, 0, 0.2)",
      borderRadius: "4px",
      outline: 0,
      padding: `${theme.spacing(2)}px ${theme.spacing(3)}px`,
      maxWidth: "600px",
    },
    modalText: {
      paddingBottom: "20px",
      paddingTop: theme.spacing(1),
      fontSize: "16px",
      fontFamily: theme.typography.fontFamily,
      fontWeight: 300,
    },
    buttonsWrapper: {
      display: "flex",
      justifyContent: "flex-end",
    },
    cancelButton: {
      marginRight: "15px",
    }
  }),
);

interface Props {
  show?: boolean;
  onPublish: (id, description) => any;
  onHide?: (val: boolean) => void;
  draftVersion: any;
}

const ModalPublishWindow = (props: Props) => {
  const {show, onHide} = props;

  const [description, setDescription] = useState("");

  const classes = useStyles();

  const onClickCancel = () => {
    onHide(false);
  };

  const setDescriptionValue = (e) => {
    setDescription(e.target.value);
  }

  const publish = () => {
    const {draftVersion, onPublish} = props;
    draftVersion && onPublish(draftVersion.id, description);

    setDescription("");
    onHide(false);
  }

  return (
    <Modal
      open={show}
      onClose={onClickCancel}
      className={classes.paper}
    >
      <div className={classes.modalContent}>
        <div className={classes.modalText}>
          You are about to push your changes onto the live site.
        </div>

        <div className={"mb-3"}>
          <EditInPlaceField
            label="Description"
            name="pageDescription"
            id="pageDescription"
            meta={{}}
            input={{
              onChange: e => setDescriptionValue(e),
              onFocus: stubFunction,
              onBlur: stubFunction,
              value: description,
            }}
            fullWidth
            multiline
          />
        </div>

        <div className={classes.buttonsWrapper}>
          <CustomButton
            styleType="cancel"
            onClick={() => onClickCancel()}
            styles={classes.cancelButton}
          >
            Cancel
          </CustomButton>
          <CustomButton
            styleType="submit"
            onClick={publish}
          >
            {"Publish"}
          </CustomButton>
        </div>
      </div>
    </Modal>
  );
};

const mapStateToProps = (state: State) => ({
  draftVersion: state.history.versions && state.history.versions.find(v => v.status === VersionStatus.draft),
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onPublish: (id, description) => dispatch(publish(id, VersionStatus.published, description)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ModalPublishWindow);
