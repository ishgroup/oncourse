import AddButton from '../addButton/AddButton';
import nativeExtend from './BuyButton.extend';
import customExtend from './BuyButton.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class buyButton extends AddButton {
    render() {
        let context = this.getContext();

        context.props.canBuy = this.props.canBuy;

        return extend.render.apply(context);
    }
}

export default buyButton;

