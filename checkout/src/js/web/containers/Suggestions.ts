/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

//Suggestions
import {IshState} from "../../services/IshState";
import {connect} from "react-redux";
import Suggestions from "../components/suggestions/Suggestions";

const mapStateToProps = (state: IshState) => ({
  phase: state.checkout.phase,
  suggestions: [
    {
      id: 1,
      title: "Creative Kids voucher",
      description: "Claim your $100 voucher from the NSW government.",
      price: 0,
      link: "#"
    },
    {
      id: 2,
      title: "NIDA Foundation donation",
      description: "Support NIDA's program for disadvantaged kids.",
      price: 30,
      link: "#"
    }
  ]
});

export default connect<any, any, any>(mapStateToProps, null)(Suggestions);
