import React from 'react';
import {createStyles, Grid, makeStyles, Modal} from '@material-ui/core';
import CustomButton from "../../components/CustomButton";

const useStyles = makeStyles(theme =>
  createStyles({
    paper: {
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      position: 'absolute',
      border: '2px solid #000',
      boxShadow: theme.shadows[5],
      padding: theme.spacing(2, 4, 3),
    },
    modalContent: {
      position: "relative",
      display: "flex",
      flexDirection: "column",
      backgroundColor: "#fff",
      backgroundClip: "padding-box",
      border: "1px solid rgba(0, 0, 0, 0.2)",
      borderRadius: "2px",
      outline: 0,
      textAlign: "center",
      padding: "30px 15px",
      maxWidth: "600px",
    },
    modalText: {
      paddingBottom: "30px",
    },
    buttonsWrapper: {
      display: "flex",
      justifyContent: "space-around",
      paddingTop: "20px",
      borderTop: "1px solid #eceeef",
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
}

const ModalWindow = (props: Props) => {
  const {title, text, show, onConfirm, onCancel, onHide} = props;

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
      {/*{title &&*/}
      {/*  <ModalHeader toggle={() => onClickCancel()}>{title}</ModalHeader>*/}
      {/*}*/}
      <Grid container className={classes.modalContent}>
        <Grid className={classes.modalText}>
          {text || 'Are you sure?'}
        </Grid>

        <Grid className={classes.buttonsWrapper}>
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
            Confirm
          </CustomButton>
        </Grid>
      </Grid>
    </Modal>
  );
};

export default ModalWindow;
