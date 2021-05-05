import React from 'react';
import {createStyles, makeStyles, Modal} from '@material-ui/core';
import CustomButton from "../../components/CustomButton";

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
      textAlign: "center",
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
  text?: string;
  title?: string;
  show?: boolean;
  onConfirm?: () => any;
  onCancel?: () => any;
  onHide?: () => any;
  children?: any;
  confirmButtonText?: string;
}

const ModalWindow = (props: Props) => {
  const {children, confirmButtonText, title, text, show, onConfirm, onCancel, onHide} = props;

  const classes = useStyles();

  const onClickCancel = () => {
    onCancel && onCancel();
    onHide();
  };

  return (
    <Modal
      open={show}
      onClose={onClickCancel}
      className={classes.paper}
    >
      <div className={classes.modalContent}>
        <div className={classes.modalTitle}>
          {title || 'Are you sure?'}
        </div>
        <div className={classes.modalText}>
          {text}
        </div>

        {children && (
          <div>
            {children}
          </div>
        )}

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
            onClick={() => onConfirm()}
          >
            {confirmButtonText || "Confirm"}
          </CustomButton>
        </div>
      </div>
    </Modal>
  );
};

export default ModalWindow;
