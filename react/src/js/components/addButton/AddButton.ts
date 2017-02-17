import * as React from 'react';
import classnames from 'classnames';
import {ConfirmOrderDialog} from './ConfirmOrderDialog';
import {stopPropagation} from '../../lib/utils';
import {Product, Course} from "../../services/IshState";

export class AddButton<ChildProps extends AddButtonProps> extends React.Component<ChildProps, AddButtonState> {
  constructor() {
    super();

    this.state = {
      showedPopup: false,
      isAlreadyAdded: false,
      pending: false
    };
  }

  add = (e: Event) => {
    if (this.state.pending) {
      return;
    }

    if (this.props.isAdded) {
      stopPropagation(e);
      this.setState({
        showedPopup: true,
        isAlreadyAdded: true
      });
    } else {
      //ToDo how to handle error?
      this.setState({
        pending: true
      });

      this.props.add({
        id: this.props.id,
        name: this.props.name,
        uniqueIdentifier: this.props.uniqueIdentifier
      })
        .then(() => {
          this.setState({
            showedPopup: true,
            isAlreadyAdded: false
          });
        })
        .always(() => {
          this.setState({pending: false});
        });
    }
  };

  closePopup = () => {
    this.setState({
      showedPopup: false
    });
  };

  getContext = () => {
    return {
      props: {
        id: this.props.id,
        name: this.props.name,
        uniqueIdentifier: this.props.name,
        isAdded: this.props.isAdded,
        isAlreadyAdded: this.state.isAlreadyAdded,
        showedPopup: this.state.showedPopup,
        paymentGatewayEnabled: this.props.paymentGatewayEnabled
      },
      methods: {
        add: this.add,
        closePopup: this.closePopup
      },
      components: {ConfirmOrderDialog},
      utils: {classnames}
    };
  }
}

export interface AddButtonProps {
  isAdded: boolean,
  add?: (item: Product | Course) => JQueryPromise<{}>,
  id: number,
  name: string,
  uniqueIdentifier: string,
  paymentGatewayEnabled: boolean
}

interface AddButtonState {
  showedPopup: boolean,
  isAlreadyAdded: boolean,
  pending: boolean
}

