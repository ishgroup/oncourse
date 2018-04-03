import * as React from "react";

interface Props {
  amexEnabled: boolean;
}

export class CvvHelp extends React.Component<Props, any> {

  render() {
    const {amexEnabled} = this.props;

    return (
      <div>
        <div id="popup-content">
          <h2>What is CVV?</h2>
          <p>
            The Card Verification Value, or CVV, is an extra code printed on
            your debit or credit card.
          </p>
          <div className="container">
            <p>
              <img
                className="imgleft"
                alt="Visa and MasterCard CVV location"
                height="126"
                width="197"
                src="/s/img/visa-cvv.png"
              />
              For <strong>Visa</strong> and <strong>MasterCard</strong> it is the final three digits of the
              number printed on the signature strip on the reverse of your card.
            </p>
          </div>

          {amexEnabled &&
            <div className="container">
              <p>
                <img
                  className="imgleft"
                  alt="American Express CVV location"
                  height="126"
                  width="197"
                  src="/s/img/amex-cvv.png"
                /> On <strong>American Express</strong> the CVV appears as a separate 4-digit code printed on the
                front of your card.
              </p>
            </div>
          }

          <p>
            As the CVV is not embossed (like the card number), the CVV is not
            printed on any receipts, hence it is not likely to be known by anyone
            other than the actual card owner.
          </p>
          <p>
            CVV is an anti-fraud measure being introduced by credit card
            companies worldwide. It is required that you enter the CVV printed on
            your card each time a payment is made and you are not present to sign a
            receipt, as for on-line transactions.
          </p>
          <p>
            We ask you to fill out the CVV here to verify that you actually
            hold the card you are using for this transaction, and to avoid anyone
            other than you from shopping with your card number.
          </p>
          <p>
            All information you submit is transferred over
            secure SSL
            connections.
          </p>
          <p>
            <strong className="alert">Note:</strong>
            The name of this code differs per
            card company. You may also know it as the Card Verification Code (CVC),
            the Card Security Code or the Personal Security Code. All names cover
            the same type of information.
          </p>
        </div>
      </div>
    );
  }
}
