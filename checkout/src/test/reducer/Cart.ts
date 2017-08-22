import {Action} from "redux";
import {cartReducer} from "../../js/web/reducers/cart";
import {Actions} from "../../js/web/actions/Actions";

xdescribe('cart reducer', () => {
  let course1, course2, product1, product2, state, initialState;

  beforeEach(() => {
    initialState = {
      courses: [],
      products: [],
      discounts: [],
    };
    product1 = course1 = {
      id: 1,
      name: 'Item 1',
    };
    product2 = course2 = {
      id: 2,
      name: 'Course 2',
    };
  });

  it('should return initial state', () => {
    expect(cartReducer(undefined, {} as Action)).toEqual(initialState);
  });

  describe('ADD_CLASS_TO_CART', () => {
    it('should add course class to cart', () => {
      state = cartReducer(undefined, {
        type: Actions.ADD_CLASS_TO_CART,
        data: course1,
      });

      state = cartReducer(state, {
        type: Actions.ADD_CLASS_TO_CART,
        data: course2,
      });

      expect(state).toEqual(Object.assign(initialState, {courses: [course1, course2]}));
    });

    it('should return previous state because of course class already is in cart', () => {
      state = cartReducer(undefined, {
        type: Actions.ADD_CLASS_TO_CART,
        data: course1,
      });

      state = cartReducer(state, {
        type: Actions.ADD_CLASS_TO_CART,
        data: course1,
      });

      expect(state).toEqual(Object.assign(initialState, {courses: [course1]}));
    });
  });

  describe('REMOVE_CLASS_FROM_CART', () => {
    it('should remove course class from cart', () => {
      state = cartReducer(undefined, {
        type: Actions.ADD_CLASS_TO_CART,
        data: course1,
      });

      state = cartReducer(state, {
        type: Actions.ADD_CLASS_TO_CART,
        data: course2,
      });

      state = cartReducer(state, {
        type: Actions.REMOVE_CLASS_FROM_CART,
        id: course1.id,
      });

      expect(state).toEqual(Object.assign(initialState, {courses: [course2]}));
    });

    it('should return previous state because of course class isn\'t in cart', () => {
      state = cartReducer(undefined, {
        type: Actions.ADD_CLASS_TO_CART,
        data: course1,
      });

      state = cartReducer(state, {
        type: Actions.REMOVE_CLASS_FROM_CART,
        id: course2.id,
      });

      expect(state).toEqual(Object.assign(initialState, {courses: [course1]}));
    });
  });

  describe('ADD_PRODUCT_TO_CART', () => {
    it('should add product to cart', () => {
      state = cartReducer(undefined, {
        type: Actions.ADD_PRODUCT_TO_CART,
        data: product1,
      });

      state = cartReducer(state, {
        type: Actions.ADD_PRODUCT_TO_CART,
        data: product2,
      });

      expect(state).toEqual(Object.assign(initialState, {products: [product1, product2]}));
    });

    it('should return previous state because of product already is in cart', () => {
      state = cartReducer(undefined, {
        type: Actions.ADD_PRODUCT_TO_CART,
        data: product1,
      });

      state = cartReducer(state, {
        type: Actions.ADD_PRODUCT_TO_CART,
        data: product1,
      });

      expect(state).toEqual(Object.assign(initialState, {products: [product1]}));
    });
  });

  describe('REMOVE_PRODUCT_FROM_CART', () => {
    it('should remove product from cart', () => {
      state = cartReducer(undefined, {
        type: Actions.ADD_PRODUCT_TO_CART,
        data: product1,
      });

      state = cartReducer(state, {
        type: Actions.ADD_PRODUCT_TO_CART,
        data: product2,
      });

      state = cartReducer(state, {
        type: Actions.REMOVE_PRODUCT_FROM_CART,
        id: product1.id,
      });

      expect(state).toEqual(Object.assign(initialState, {products: [product2]}));
    });

    it('should return previous state because of product isn\'t in cart', () => {
      state = cartReducer(undefined, {
        type: Actions.ADD_PRODUCT_TO_CART,
        data: product1,
      });

      state = cartReducer(state, {
        type: Actions.REMOVE_PRODUCT_FROM_CART,
        id: product2.id,
      });

      expect(state).toEqual(Object.assign(initialState, {products: [product1]}));
    });
  });
});
