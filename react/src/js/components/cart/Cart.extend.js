export default {
    render() {
        let countClasses = this.props.classes.length,
            count = countClasses + this.props.products.length,
            { showedShortList } = this.props,
            { CartClassItem, CartProductItem } = this.components;

        return (
            <div className="short-list" id="shortlist">
                <div className="shortlistInfo" id="info">
                    <span>{count}</span>
                    <p>{this.utils.plural(count, ['item', 'items'])}</p>
                </div>
                {!!count && [<h3 key="title" className="title-block">
                    <span>My Courses Order</span>
                </h3>,
                <div key="body" className="shortlistChoices dialogContainer">
                    <ul className="shortListOrder shortlistChoices" style={{
                        'display': showedShortList ? 'block' : 'none'
                    }}>
                        {this.props.classes.map((courseClass) => {
                            return <CartClassItem key={courseClass.id} item={courseClass} remove={this.methods.removeClass}/>
                        })}
                        {this.props.products.map((product) => {
                            return <CartProductItem key={product.id} item={product} remove={this.methods.removeProduct}/>
                        })}
                        <li className="shortListOrderEnrol">
                            <a className="shortlistLinkEnrol" href="/enrol/">{countClasses ? 'Enrol' : 'Purchase'}</a>
                        </li>
                    </ul>
                    <div className="closeButton" onClick={this.methods.toggleShortList}>X</div>
                </div>,
                <div key="footer" className="shortlistAction">
                    <ul className="shortlistControls">
                        <li className={this.utils.classnames('active', {
                            shortlistActionHide: showedShortList,
                            shortlistActionShow: !showedShortList
                        })} onClick={this.methods.toggleShortList}>
                            <a>{(showedShortList ? 'Hide' : 'Show') + ' Shortlist'}</a>
                        </li>
                        <li className="shortlistActionEnrol">
                            <a href="/enrol/">{countClasses ? 'Enrol' : 'Purchase'}</a>
                        </li>
                    </ul>
                </div>]}
            </div>
        );
    }
};

