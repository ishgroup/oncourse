import {Phase} from '../enrol/reducers/State';
import {Tabs} from '../enrol/containers/payment/reducers/State';

interface ProductEvent {
  id: string;                   // Product ID (string).
  name: string;                 // Product name (string).
  category: string;             // Product category (string).
  brand?: string;               // Product brand (string).
  variant?: string;             // Product variant (string).
  price?: number;               // Product price (currency).
  quantity?: number;
}

let trackingId;
let trackingName;

export const initGAEvent = (data, state) => {

  // proxy for debug mode
  // window['ga'] = window['ga'] ? window['ga'] : (...params) => {console.log(params);};
  const cart = state.cart;
  const amount = state.checkout.amount;

  // TODO: move to config.js
  trackingName = 'gtmEC';

  trackingId = state.preferences.trackingId;

  try {
    if (window['ga'] && data) {

      switch (data.ecAction) {
        case 'addProduct':
          return sendItemToCartEvent(data);

        case 'removeProduct':
          return sendRemoveItemFromCartEvent(data);

        case 'checkoutStep':
          return sendCheckoutStepEvent(data, cart);

        case 'checkoutStepOption':
          return sendCheckoutStepOptionEvent(data, cart);

        case 'purchase':
          return sendPurchaseCartEvent(data, cart, amount);
      }
    }
  } catch (e) {
    console.log('unhandled error in google analytics service ' + e);
  }
};

const sendInitActions = () => {
  // window['ga']('create', trackingId, 'auto');
  // window['ga']('require', 'ec');
};

const sendItemToCartEvent = (data: ProductEvent) => {
  // sendInitActions();

  // window['ga'](`${trackingName}.ec:addProduct`, {
  //   'id': data.id,
  //   'name': data.name,
  //   'category': data.category,
  //   'price': data.price,
  //   'quantity': 1
  // });
  //
  // window['ga'](`${trackingName}.ec:setAction`, 'add');

  window['dataLayer'].push({
    'event': 'addToCart',
    'ecommerce': {
      'currencyCode': 'USD',
      'add': {
        'products': [{
          'name': data.name,
          'id': data.id,
          'price': data.price,
          'category': data.category,
          'quantity': 1,
        }],
      },
    },
  });

  // window['ga']('send', {
  //   'hitType': 'event',
  //   'eventCategory': 'ecommerce',
  //   'eventAction': 'add item to cart',
  // });
};

const sendRemoveItemFromCartEvent = (data: ProductEvent) => {
  // sendInitActions();

  // window['ga'](`${trackingName}.ec:addProduct`, {
  //   'id': data.id,
  //   'name': data.name,
  //   'category': data.category,
  //   'price': data.price,
  //   'quantity': 1
  // });
  //
  // window['ga'](`${trackingName}.ec:setAction`, 'remove');
  //
  // window['ga'](`${trackingName}.send`, {
  //   'hitType': 'event',
  //   'eventCategory': 'ecommerce',
  //   'eventAction': 'remove item from cart',
  // });

  // Measure the removal of a product from a shopping cart.
  window['dataLayer'].push({
    'event': 'removeFromCart',
    'ecommerce': {
      'remove': {                               // 'remove' actionFieldObject measures.
        'products': [{                          //  removing a product to a shopping cart.
          'name': data.name,
          'id': data.id,
          'price': data.price,
          'category': data.category,
          'quantity': 1,
        }],
      },
    },
  });
};

const sendCheckoutStepEvent = (data, cart) => {
  const {step} = data;

  if (!step || !step.step) return;

  // sendInitActions();
  // sendAddProductsFromCart(cart);

  // window['ga'](`${trackingName}.ec:setAction`, 'checkout', {
  //   step: step.step,
  //   option: step.initialOption,
  // });
  //
  // window['ga'](`${trackingName}.send`, {
  //   hitType: 'event',
  //   eventCategory: 'ecommerce',
  //   eventAction: 'set checkout step',
  //   eventLabel: step.initialOption,
  // });

  const products = getProducts(cart);

  window['dataLayer'].push({
    'event': 'checkout',
    'ecommerce': {
      'checkout': {
        'actionField': {
          'step': step.step,
          'option': step.initialOption
        },
        'products': products.map(product => ({
          'name': product.name,
          'id': product.id,
          'price': product.price,
          'category': product.category,
          'quantity': 1,
        })),
      },
    },
  });
};

const sendCheckoutStepOptionEvent = (data, cart) => {
  const {step} = data;

  if (!step || !step.option) return;

  // sendInitActions();
  // sendAddProductsFromCart(cart);
  //
  // window['ga'](`${trackingName}.ec:setAction`, 'checkout_option', {
  //   step: step.step,
  //   option: step.option,
  // });
  //
  // window['ga'](`${trackingName}.send`, {
  //   hitType: 'event',
  //   eventCategory: 'ecommerce',
  //   eventAction: 'set checkout step',
  //   eventLabel: step.option,
  // });

  window['dataLayer'].push({
    'event': 'checkoutOption',
    'ecommerce': {
      'checkout_option': {
        'actionField': {
          'step': step.step,
          'option': step.option
        },
      },
    },
  });
};

const sendPurchaseCartEvent = (data, cart, amount) => {
  // sendInitActions();
  // sendAddProductsFromCart(cart);

  // window['ga'](`${trackingName}.ec:setAction`, 'purchase', {
  //   id: data.id,
  //   affiliation: data.type,
  //   revenue: amount.total,
  // });

  // window['ga'](`${trackingName}.send`, {
  //   hitType: 'event',
  //   eventCategory: 'ecommerce',
  //   eventAction: 'checkout complete',
  //   eventLabel: data.type,
  // });

  const products = getProducts(cart);

  window['dataLayer'].push({
    'event': 'purchase',
    'ecommerce': {
      'purchase': {
        'actionField': {
          'id': data.id,                        // Transaction ID. Required for purchases and refunds.
          'affiliation': data.type,
          'revenue': amount.total,              // Total transaction value (incl. tax and shipping)
        },
        'products': products.map(product => ({
          'name': product.name,
          'id': product.id,
          'price': product.price,
          'category': product.category,
          'quantity': 1,
        })),
      },
    },
  });
};

const getProducts = cart => {
  const items = getItemsFromCart(cart);
  const products = [];

  if (items.products && items.products.length) {
    items.products.map(product => {
      products.push({
        id: product.id,
        name: product.name,
        category: 'product',
        price: product.price || 0,
        quantity: 1,
      });
    });
  }

  if (items.courses && items.courses.length) {
    items.courses.map(course => {
      const price = getCoursePrice(course.price);

      products.push({
        id: course.id,
        name: course.course.name,
        category: 'Course Class',
        price,
        quantity: 1,
      });
    });
  }

  return products;

};

const sendAddProductsFromCart = cart => {
  const items = getItemsFromCart(cart);

  if (items.products && items.products.length) {
    items.products.map(product => {
      window['ga'](`${trackingName}.ec:addProduct`, {
        id: product.id,
        name: product.name,
        category: 'product',
        price: product.price || 0,
        quantity: 1,
      });
    });
  }

  if (items.courses && items.courses.length) {
    items.courses.map(course => {
      const price = getCoursePrice(course.price);

      window['ga'](`${trackingName}.ec:addProduct`, {
        id: course.id,
        name: course.course.name,
        category: 'Course Class',
        price,
        quantity: 1,
      });
    });
  }
};


/**
 *
 *  Builder for Google analytics actions data (e-commerce plugin)
 *
 **/
export class GABuilder {
  static addProductToCart = (type, item) => {
    return {
      ecAction: 'addProduct',
      id: item.id,
      name: item.name,
      category: type,
      quantity: 1,
    };
  }

  static removeProductFromCart = (type, item) => {
    return {
      ecAction: 'removeProduct',
      id: item.id,
      name: item.name,
      category: type,
      quantity: 1,
    };
  }

  static addCourseClassToCart = (type, item) => {
    return {
      ecAction: 'addProduct',
      id: item.id,
      name: item.course && item.course.name,
      category: type,
      price: getCoursePrice(item.price),
      quantity: 1,
    };
  }

  static removeCourseClassFromCart = (type, item) => {
    return {
      ecAction: 'removeProduct',
      id: item.id,
      name: item.course && item.course.name,
      category: type,
      price: getCoursePrice(item.price),
      quantity: 1,
    };
  }

  static setCheckoutStep = (phase, opt = null) => {
    const step = getCheckoutStepFromPhase(phase);
    const option = getFormattedCheckoutOption(opt);

    if (option) {
      step.option = option;
    }

    return {
      ecAction: step.option ? 'checkoutStepOption' : 'checkoutStep',  // action key
      step,
    };
  }

  static purchaseItems = response => {
    if (response && response.status === 'SUCCESSFUL') {
      return {
        ecAction: 'purchase',
        id: response.sessionId,
        status: 'success',
        type: 'credit card',
      };
    }

    if (response && response.status === 'SUCCESSFUL_BY_PASS') {
      return {
        ecAction: 'purchase',
        id: response.sessionId,
        status: 'success',
        type: 'corporate pass',
      };
    }

    return null;
  }
}


/**
 *
 *  Helper functions
 *
 **/
const getItemsFromCart = cart => {
  const products = cart.products.result.map(id => cart.products.entities[id]);
  const courses = cart.courses.result.map(id => cart.courses.entities[id]);

  return {products, courses};
};

const getCoursePrice = price => {
  const fullPrice = price.feeOverriden ? Number(price.feeOverriden).toFixed(2) : Number(price.fee).toFixed(2);
  const discountedPrice = price.appliedDiscount ? Number(price.appliedDiscount.discountedFee).toFixed(2) : null;

  return price && discountedPrice ? discountedPrice : fullPrice;
};

const getCheckoutStepFromPhase = phase => {
  switch (phase) {
    case Phase.Init:
      return {step: 1, initialOption: 'Init Checkout Process', option: null};

    case Phase.AddPayer:
      return {step: 2, initialOption: 'Add Payer', option: null};

    case Phase.EditContact:
      return {step: 3, initialOption: 'Edit Contact Details', option: null};

    case Phase.Summary:
      return {step: 4, initialOption: 'Summary Page', option: null};

    case Phase.ChangeParent:
      return {step: 4, initialOption: null, option: 'Change Parent'};

    case Phase.AddConcession:
      return {step: 4, initialOption: null, option: 'Add concession'};

    case Phase.AddContact:
      return {step: 4, initialOption: null, option: 'Add contact'};

    case Phase.Payment:
      return {step: 5, initialOption: 'Payment Page (Credit Card)', option: null};

    case Phase.AddContactAsCompany:
      return {step: 5, initialOption: null, option: 'Add a company'};

    case Phase.AddContactAsPayer:
      return {step: 5, initialOption: null, option: 'Add a payer'};

    // case Phase.Result:
    //   return {step: 6, initialOption: 'Result page', option: null};

    default:
      return {step: null, initialOption: null, option: null};
  }
};

const getFormattedCheckoutOption = option => {
  switch (option) {
    case Tabs.corporatePass:
      return 'Payment Page (Corporate Pass);';

    case Tabs.creditCard:
      return 'Payment Page (Credit Card)';

    default:
      return null;
  }
};
