import {connect} from "react-redux";
import Promotions from "../components/promotions/Promotions";
import {IshState, PromotionCart} from "../../services/IshState";
import {Actions} from "../actions/Actions";

export default connect<any, any, any, IshState>(mapStateToProps, mapDispatchToProps)(Promotions as any);

function mapStateToProps(state: IshState) {
  return {
    promotions: state.cart.promotions,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addPromotion: (code: string) => {
      dispatch({
        type: Actions.ADD_PROMOTION_TO_CART,
        payload: code,
      });
    },
    removePromotion: (promotion: PromotionCart) => {
      dispatch({
        type: Actions.REMOVE_PROMOTION_FROM_CART,
        payload: promotion,
      });
    },
  };
}
