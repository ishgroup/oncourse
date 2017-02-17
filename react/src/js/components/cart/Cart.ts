import * as React from 'react';
import CartClassItem from './CartClassItem';
import CartProductItem from './CartProductItem';
import classnames from 'classnames';
import {plural} from '../../lib/utils';
import nativeExtend from './Cart.extend';
import {Product} from "../../services/IshState";

const custom = {}; // require('./Cart.custom')
const extend = Object.assign({}, nativeExtend, custom);

class Cart extends React.Component<CarsComponentProps, CartComponentState> {

  constructor() {
    super();

    this.state = {
      showedShortList: false
    };
  }


  toggleShortList = () => {
    this.setState({
      showedShortList: !this.state.showedShortList
    });
  };

  componentWillReceiveProps(nextProps) {
    if (!nextProps.classes.length) {
      this.setState({
        showedShortList: false
      });
    }
  }

  render() {
    return extend.render.apply({
      props: {
        classes: this.props.classes,
        products: this.props.products,
        showedShortList: this.state.showedShortList
      },
      methods: {
        toggleShortList: this.toggleShortList,
        removeClass: this.props.removeClass,
        removeProduct: this.props.removeProduct
      },
      utils: {plural, classnames},
      components: {CartProductItem, CartClassItem}
    });
  }
}

interface CarsComponentProps {
  classes: string[];
  products: Product[];
  removeClass: () => void; // todo
  removeProduct: () => void; // todo
}

interface CartComponentState {
  showedShortList: boolean;
}

export default Cart;
