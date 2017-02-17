import {AddButton, AddButtonProps} from '../addButton/AddButton';
import nativeExtend from './BuyButton.extend';

const custom = {}; // require("./BuyButton.custom")
const extend = Object.assign({}, nativeExtend, custom);

type CommonProps = BuyButtonProps & AddButtonProps;

// TODO: Delete common component
export class BuyButton extends AddButton<CommonProps> {
  render() {
    let context = this.getContext();

    Object.assign(context.props, {
      canBuy: this.props.canBuy
    });

    return extend.render.apply(context);
  }
}

export interface BuyButtonProps {
  id: number; //todo
  canBuy?: boolean;
}
