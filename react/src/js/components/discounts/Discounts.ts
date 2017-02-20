import * as React from 'react';
import nativeExtend from './Discounts.extend';
import {Discount} from "../../services/IshState";

const extend = Object.assign({}, nativeExtend, require('./Discounts.custom'));

export default class Discounts extends React.Component<DiscountsProps, DiscountState> {

  constructor() {
    super();

    this.state = {
      value: '',
      error: null,
      pending: false
    };
  }

  onChange = (e) => {
    this.setState({
      value: e.target.value
    });
  };

  add = (e) => {
    e.preventDefault();
    this.setState({pending: true});
    this.props.add(this.state.value)
      .fail(() => {
        // ToDo handle error
        this.setState({error: 'Error'});
      })
      .always(() => {
        this.setState({pending: false});
      });
  };

  render() {
    return extend.render.apply({
      props: {
        discounts: this.props.discounts,
        pending: this.state.pending,
        error: this.state.error,
        value: this.state.value
      },
      methods: {
        onChange: this.onChange,
        add: this.add
      }
    });
  }
}

interface DiscountsProps {
  discounts: Discount[];
  add: (value: any) => JQueryPromise<{}>; // todo
}

interface DiscountState {
  value: string;
  error: any; // todo
  pending: boolean;
}
