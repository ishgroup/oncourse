import {Phase} from '../enrol/reducers/State';
import {Tabs} from '../enrol/containers/payment/reducers/State';
import {IshState} from "./IshState";
import {findPriceInDOM} from "../common/utils/DomUtils";
import Bugsnag from "@bugsnag/js";

interface ProductEvent {
  id: string;                   // Product ID (string).
  name: string;                 // Product name (string).
  category: string;             // Product category (string).
  brand?: string;               // Product brand (string).
  variant?: any;             // Product variant (string).
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
  const summary = state.checkout.summary;

  // TODO: move to config.js
  trackingName = 'gtmEC';
  trackingId = state.preferences.trackingId;

  try {
    if (data) {

      switch (data.ecAction) {
        case 'addProduct':
          return sendItemToCartEvent(data);

        case 'removeProduct':
          return sendRemoveItemFromCartEvent(data);

        case 'checkoutStep':
          return sendCheckoutStepEvent(data, cart, summary);

        case 'checkoutStepOption':
          return sendCheckoutStepOptionEvent(data);

        case 'purchase':
          return sendPurchaseCartEvent(data, cart, amount, summary);

        case 'linkClick':
          return sendProductClickEvent(data, state);
      }
    }
  } catch (e) {
    console.log('unhandled error in google analytics service ' + e, data);
  }
};

const sendProductClickEvent = (data, state: IshState) => {
  switch (data.type) {
    case "class": {
      const classes = state.courses.entities;

      let courseClass;

      Object.keys(classes).forEach(k => {
        if (data.code.includes(classes[k].course.code) && data.code.includes(classes[k].code)) {
          courseClass = classes[k];
        }
      });

      if (courseClass) {
        window['dataLayer'].push({
          event: 'productClick',
          ecommerce: {
            click: {
              products: [{
                id: courseClass.id,
                name: courseClass.course.name + " " + courseClass.course.code + "-" + courseClass.code,
                category: "class",
                variant: courseClass.subject,
                price: courseClass.price.fee,
                quantity: 1,
              }],
            },
          },
        });
      }
      break;
    }
    case "product": {
      const products = state.products.entities;

      let product;

      Object.keys(products).forEach(k => {
        if (data.code === (products[k].code)) {
          product = products[k];
        }
      });

      if (product) {
        window['dataLayer'].push({
          event: 'productClick',
          ecommerce: {
            click: {
              products: [{
                id: product.id,
                name: product.name,
                category: "",
                variant: product.type,
                quantity: 1,
                price: findPriceInDOM(product.id),
              }],
            },
          },
        });
      }
    }
  }

};

export const sendProductImpressionEvent = (data: ProductEvent) => {
  window['dataLayer'].push({
    ecommerce: {
      currencyCode: 'AUD',
      impressions: [{
        name: data.name,
        id: data.id,
        price: data.price,
        variant: data.variant ? data.variant.toLowerCase() : "",
        category: data.category,
        quantity: 1,
      }],
    },
  });
};

export const sendProductDetailsImpressionEvent = (data: ProductEvent) => {
  window['dataLayer'].push({
    ecommerce: {
      detail: {
        products: [{
          name: data.name,
          id: data.id,
          price: data.price,
          variant: data.variant ? data.variant.toLowerCase() : "",
          category: data.category,
          quantity: 1,
        }],
      },
    },
  });
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
    event: 'addToCart',
    ecommerce: {
      currencyCode: 'AUD',
      add: {
        products: [{
          name: data.name,
          id: data.id,
          price: data.price,
          variant: data.variant ? data.variant.toLowerCase() : "",
          category: data.category,
          quantity: 1,
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
    event: 'removeFromCart',
    ecommerce: {
      remove: {                               // 'remove' actionFieldObject measures.
        products: [{                          //  removing a product to a shopping cart.
          name: data.name,
          id: data.id,
          price: data.price,
          variant: data.variant ? data.variant.toLowerCase() : "",
          category: data.category,
          quantity: 1,
        }],
      },
    },
  });
};

const sendCheckoutStepEvent = (data, cart, summary) => {
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

  const productsBase = getProducts(cart);
  const productsSummary = getProductsSummary(summary);

  const products = productsBase.filter(product => productsSummary[product.id]).map(product => {
    return {
      name: product.name,
      id: product.id,
      price: productsSummary[product.id].price,
      variant: product.variant,
      category: product.category,
      quantity: productsSummary[product.id].quantity,
    };
  });

  if (step.step === 5) {
    // tslint:disable-next-line:ter-prefer-arrow-callback
    Bugsnag.notify(new Error('Checkout log'), function (event) {
      event.errors[0].errorClass = 'Checkout log';

      // Add additional diagnostic information
      event.addMetadata('Checkout data', {
        step: step.step,
        option: step.initialOption,
        products: products.map(p => p.name).toString(),
      });
    });
  }

  window['dataLayer'].push({
    event: 'checkout',
    ecommerce: {
      checkout: {
        actionField: {
          step: step.step,
          option: step.initialOption,
        },
        products,
      },
    },
  });
};

const sendCheckoutStepOptionEvent = data => {
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
    event: 'checkoutOption',
    ecommerce: {
      checkout_option: {
        actionField: {
          step: step.step,
          option: step.option,
        },
      },
    },
  });
};

const sendPurchaseCartEvent = (data, cart, amount, summary) => {
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

  const productsBase = getProducts(cart);
  const productsSummary = getProductsSummary(summary);

  const products = productsBase.filter(product => productsSummary[product.id]).map(product => ({
    name: product.name,
    id: product.id,
    price: productsSummary[product.id].price,
    variant: product.type ? product.type.toLowerCase() : "",
    category: product.subject,
    quantity: productsSummary[product.id].quantity,
  }));

  Bugsnag.notify(new Error('Checkout log'), function (event) {
    event.errors[0].errorClass = 'Checkout log';

    // Add additional diagnostic information
    event.addMetadata('Checkout data', {
      step: "purchase success",
      products: products.map(p => p.name).toString(),
    });
  });

  window['dataLayer'].push({
    event: 'purchase',
    ecommerce: {
      purchase: {
        actionField: {
          id: data.id,                        // Transaction ID. Required for purchases and refunds.
          affiliation: data.type,
          revenue: amount.total,              // Total transaction value (incl. tax and shipping)
        },
        products,
      },
    },
  });
};

const getProductsSummary = summary => {
  const productsSummary = {};

  const productTypes = ["articles","enrolments","memberships","vouchers"];

  Object.keys(summary.entities).forEach(e => {
    if (e === "contactNodes") {
      Object.keys(summary.entities[e]).forEach(k => {
        Object.keys(summary.entities[e][k]).forEach(entity => {
          if (productTypes.includes(entity)
            && summary.entities[e][k][entity].length) {
            summary.entities[e][k][entity].forEach(id => {
              const product = summary.entities[entity][id];
              if (!product.selected) {
                return;
              }
              const productId = product.productId || product.classId;

              if (!productsSummary.hasOwnProperty(productId)) {
                productsSummary[productId] = {
                  price: 0,
                  quantity: 0,
                };
              }
              if (product.classId) {
                productsSummary[productId].price = productsSummary[productId].price + product.price.fee;
                productsSummary[productId].quantity++;
              } else {
                productsSummary[productId].price = product.total || product.price;
                productsSummary[productId].quantity =  product.quantity || 1;
              }
            });
          }
        });
      });
    }
  });

  return productsSummary;
};

const getProducts = cart => {
  const items = getItemsFromCart(cart);
  const products = [];

  if (items.products && items.products.length) {
    items.products.map(product => {
      products.push({
        id: product.id,
        name: product.name,
        variant: product.type,
        category: "",
      });
    });
  }

  if (items.courses && items.courses.length) {
    items.courses.map(course => {
      products.push({
        id: course.id,
        name: course.course.name,
        category: course.subject,
        variant: "class",
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
        variant: product.type,
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
        variant: 'class',
        category: course.subject,
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
  static addProductToCart = item => {
    return {
      ecAction: 'addProduct',
      id: item.id,
      name: item.name,
      variant: item.type ? item.type.toLowerCase() : "" ,
      category: "",
      quantity: 1,
    };
  }

  static removeProductFromCart = item => {
    return {
      ecAction: 'removeProduct',
      id: item.id,
      name: item.name,
      variant:  item.type ? item.type.toLowerCase() : "" ,
      category: "",
      quantity: 1,
    };
  }

  static addCourseClassToCart = (type, item) => {
    return {
      ecAction: 'addProduct',
      id: item.id,
      name: item.course && item.course.name,
      variant: "class",
      category: item.subject,
      price: getCoursePrice(item.price),
      quantity: 1,
    };
  }

  static removeCourseClassFromCart = (type, item) => {
    return {
      ecAction: 'removeProduct',
      id: item.id,
      name: item.course && item.course.name,
      variant: "class",
      category: item.subject,
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
        id: response.reference,
        status: 'success',
        type: 'credit card',
      };
    }

    if (response && response.status === 'SUCCESSFUL_BY_PASS') {
      return {
        ecAction: 'purchase',
        id: response.reference,
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
