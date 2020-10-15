def run(args) {
    def article = args.entity

    if (PaymentSource.SOURCE_WEB.equals(article.invoiceLine.invoice.source)) {
        switch (article.product.sku) {
            case "SFEHC":
                smtp {
                    to "admin@byroncollege.org.au"
                    subject "Sustainability for Educators Book purchased"
                    content "Hi Admin\n\n${article.contact.fullName} has just purchased the hard copy of the Sustainability For Educators Book, can you please send them a copy as soon as possible.\n\nThanks"
                }
                break
            case "SFEEV":
                email {
                    to article.contact
                    from "admin@byroncollege.org.au"
                    template "Sustainability for educators book"
                    bindings article : article
                }
                break
            default:
                //do nothing
                break
        }
    }
}