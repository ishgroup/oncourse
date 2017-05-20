import * as React from "react";
import withRouter from "react-router/lib/withRouter";
import {ReactRouterContext} from "../../../types";
import {Paths} from "../../../config/Paths";

/**
 * @Deprecated
 */
class SummaryComponent extends React.Component<SummaryProps & ReactRouterContext, SummaryState> {
  constructor() {
    super();
  }

  render() {
    const {router} = this.props;

    return (
      <div>
        <table className="payments">
          <tbody>
          <tr className="student-name">
            <td colSpan={3}>
              <div className="student-info">
                Test Test <span className="student-email">(test@test.com)</span>
              </div>
              <div><a className="add-concession" href="#">Add Concession</a>
                <a style={{display:'none'}} href="/enrol/checkout.contactlist.contactitem.editconcessionlink/0"></a>
              </div>
            </td>
          </tr>
          </tbody>
          <tbody className="checkoutList">
          <tr className="enrolmentItem ">
            <td className="enrolmentInfo">
              <label>
                <input checked className="enrolmentSelect" type="checkbox"/>
                Drawing - Urban Sketching
                <a style={{display:'none'}} className="selectEnrolmentLink" href="/enrol/checkout.contactlist">Display none</a>
              </label>
              <span>(Note: This class has already commenced)</span>
              <br/>
              <em> 2. Sydney City Campus York Street »
                <span className="started">Sat 4 Mar 17 10:00 AM</span>
                -
                <span className="ended">Sat 11 Mar 17 1:30 PM</span>
              </em>
            </td>
            <td className="alignright">
              <span id="fullPrice_0_0" className="fee-full fullPrice">$129</span>
              <span data-index="0_0" id="discountedPrice_0_0"
                    className="fee-discounted collapse discountedPrice">$129</span>
              <span style={{display:'none'}} id="discount_0_0" className="discount">0.00</span>
            </td>
          </tr>
          </tbody>
          <tbody id="totals" className="t-update t-zone">
          <tr>
            <td colSpan={3}>
              <div id="addContact">Add another student</div>
              <a style={{display:'none'}} href="/enrol/checkout:addcontactevent">Display None</a>
            </td>
          </tr>
          </tbody>
        </table>
        <div className="amount-container">
          <div className="code-info">
            <input maxLength={64} className="code_input" name="add_code" type="text"/>
            <div className="button" id="addCode">Add Code</div>
            <a style={{display:'none'}} href="/enrol/checkout.addcode:addcodeevent">Display None</a>
            <p>Promotional Code,Gift Certificate or Voucher</p>
          </div>
          <div className="amount-content">
            <p className="total-amount">
              <label>Total</label><span>$129</span>
            </p>
            <p>
              <label>Pay Now</label><span>$129</span>
            </p>
            <div id="access-password-popup">
              <div className="code-info">
                <input className="code_input" name="password" type="password"/>
                <div className="button" id="submitPassword">Submit</div>
                <a style={{display:'none'}} href="/enrol/checkout.amount:creditaccessevent">Display None</a>
                <p>
                  Password <em title="This field is required">*</em>
                </p>
                <p>
                  <a target="_blank"
                     href="/portal/login?firstName=Test&amp;lastName=Test&amp;emailAddress=test@test.com">Forgot
          password</a>
                </p>
              </div>
            </div>
          </div>
          <div className="clearboth"/>
          <a onClick={() => router.push(Paths.payment)}
            id="proceedToPaymentEvent"
            className="btn btn-primary">
            Proceed to Payment
          </a>
        </div>
      </div>
    );
  }
}

interface SummaryProps {

}

interface SummaryState {
}


export const Summary = withRouter(SummaryComponent);

