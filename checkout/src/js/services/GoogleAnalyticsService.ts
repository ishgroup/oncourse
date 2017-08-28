import {Phase} from "../enrol/reducers/State";
import {Tabs} from "../enrol/containers/payment/reducers/State";

interface ProductEvent {
  id: string,                   // Product ID (string).
  name: string,                 // Product name (string).
  category: string,             // Product category (string).
  brand?: string,               // Product brand (string).
  variant?: string,             // Product variant (string).
  price?: number,               // Product price (currency).
  quantity?: number,
}

// TODO: get all item from cart, for each phase
export const initGAEvent = data => {
  console.log(data);

  try {
    if (window['ga'] && data) {
      window['ga']('create', 'UA-XXXXX-YY', 'auto');
      window['ga']('require', 'ec');

      switch (data.ecAction) {
        case 'addProduct':
          return sendItemToCartEvent(data);

        case 'setCheckoutStep':
          return setCheckoutStepEvent(data);

        case 'setCheckoutStepOption':
          return setCheckoutStepOptionEvent(data);
      }

      window['ga']('send', 'pageview');
    }
  } catch (e) {
    console.log('unhandled error in google analytics service ' + e);
  }



};

const sendItemToCartEvent = (data: ProductEvent) => {
  window['ga']('ec:addProduct', {  // Provide product details in an productFieldObject.
    id: data.id,                   // Product ID (string).
    name: data.name,               // Product name (string).
    category: data.category,       // Product category (string).
    quantity: 1                    // Product quantity (number).
  });
};

const setCheckoutStepEvent = (data) => {
  const {step} = data;

  if (step && step.step) {
    window['ga']('ec:setAction', 'checkout', {
      step: step.step,
      option: step.initialOption,
    });
  }
};

const setCheckoutStepOptionEvent = (data) => {
  const {step} = data;

  if (step && step.option) {
    window['ga']('ec:setAction', 'checkout_option', {
      step: step.step,
      option: step.option,
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
    console.log(item);
    return {
      ecAction: 'addProduct',       // action key
      id: item.id,                  // Product ID (string).
      name: item.name,              // Product name (string).
      category: type,               // Product category (string).
      // price: '0.00',             // Product price (currency).
      quantity: 1,
    }
  };

  static addCourseClassToCart = (type, item) => {
    console.log(item);
    const price = item.price;
    const fullPrice = price.feeOverriden ? Number(price.feeOverriden).toFixed(2) : Number(price.fee).toFixed(2);
    const discountedPrice = price.appliedDiscount ? Number(price.appliedDiscount.discountedFee).toFixed(2) : null;


    return {
      ecAction: 'addProduct',                                             // action key
      id: item.id,                                                        // Product ID (string).
      name: item.course && item.course.name,                              // Product name (string).
      category: type,                                                     // Product category (string).
      price: price && discountedPrice ? discountedPrice : fullPrice,      // Product price (currency).
      quantity: 1,
    }
  };

  static setCheckoutStep = (phase, opt = null) => {
    const step = getCheckoutStepFromPhase(phase);
    const option = getFormattedCheckoutOption(opt);

    if (option) {
      step.option = option;
    }

    return {
      ecAction: step.option ? 'setCheckoutStepOption' : 'setCheckoutStep',  // action key
      step: step
    }
  }

}

/**
 *
 *  Helper function for formatting data
 *
 **/
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

    case Phase.Result:
      return {step: 6, initialOption: 'Result page', option: null};

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