import {connect} from "react-redux";
import Promotions from "../components/promotions/Promotions";
import {IshState, PromotionCart} from "../../services/IshState";
import {IshActions} from "../../constants/IshActions";

export default connect(mapStateToProps, mapDispatchToProps)(Promotions as any);

function mapStateToProps(state: IshState) {
  return {
    promotions: state.cart.promotions,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addPromotion: (code: string) => {
      dispatch({
        type: IshActions.ADD_PROMOTION_TO_CART,
        payload: code
      });
    },
    removePromotion: (promotion: PromotionCart) => {
      dispatch({
        type: IshActions.REMOVE_PROMOTION_FROM_CART,
        payload: promotion
      });
    }
  };
}
