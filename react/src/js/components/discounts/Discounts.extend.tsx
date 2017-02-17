import * as React from 'react';

export default {
    render() {
        const count = this.props.discounts.length;
        const { error, value, pending } = this.props;

        return (
            <div>
                <p>
                    Sometimes we are able to offer discounts on a selection of our classes.
                    If you have a discount code, then please enter it into the box below
                </p>
                <form id="addDiscountForm">
                    <label htmlFor="promo">Discount code:</label>
                    <input id="promo" value={value} onChange={this.methods.onChange}/>
                    <button id="addDiscountButton" disabled={!value || pending}>Add</button>
                    {!!error && <div className="validation">{error}</div>}
                </form>

                {!!count && <div>
                    <p className="discount_options">You have entered the following codes.</p><br/>
                    {this.props.discounts.map((discount) => {
                        return (
                            <div key={discount.id}>
                                <h5 className="popup-name">{discount.addedPromotion.code}</h5>
                                <div className="clear"></div>
                                <p>{discount.addedPromotion.detail}</p>
                                <div className="divideline"></div>
                            </div>
                        );
                    })}
                    <p>The discounted prices will be now shown next to each course as you browse this site.</p>
                </div>}

                <p className="note">
                    <strong className="alert">Please note:</strong>
                    Our discounts are usually only available until a certain date, so you may not
                    be able to use an old discount code.
                </p>
            </div>
        );
    }
};
