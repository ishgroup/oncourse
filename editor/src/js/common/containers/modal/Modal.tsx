import React from 'react';
import {Button, Modal as BModal, ModalHeader, ModalBody, ModalFooter} from 'reactstrap';

interface Props {
  text?: string;
  title?: string;
  show?: boolean;
  onConfirm?: () => any;
  onCancel?: () => any;
  onHide?: () => any;
}

export const Modal = (props: Props) => {
  const {title, text, show, onConfirm, onCancel, onHide} = props;

  const onClickCancel = () => {
    onCancel && onCancel();
    onHide();
  };

  return (
    <BModal isOpen={show} toggle={() => onClickCancel()} wrapClassName="cms-scope">

      {title &&
        <ModalHeader toggle={() => onClickCancel()}>{title}</ModalHeader>
      }

      <ModalBody>
        {text || 'Are you sure?'}
      </ModalBody>

      <ModalFooter>
        <Button color="link" onClick={() => onClickCancel()}>Cancel</Button>{' '}
        <Button color="primary" onClick={() => onConfirm()}>Confirm</Button>
      </ModalFooter>

    </BModal>
  );
};
