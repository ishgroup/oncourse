import ish.oncourse.server.report.PdfUtil

def salesList = query {
    entity "ProductItem"
    query "taggingRelations.tag.name is \"Waiting for Shipping\""
}

if(!salesList.isEmpty()){
    salesList.each { sale ->
        sale.removeTag("Shipping Status/Waiting for Shipping")
        sale.addTag("Shipping Status/In progress")
    }
    salesList.first().context.commitChanges()
}

List<byte[]>pdfs = new ArrayList<>()

def pickupSales = salesList.findAll { sale -> sale.product.hasTag("Pickup", true) }
if(!pickupSales.isEmpty()){
    def pickupReportsData = report {
        keycode "ish.oncourse.slipPickupReport"
        records pickupSales
        generatePreview true
    }

    pdfs.add(pickupReportsData)
}


def shippedSales = salesList.findAll { sale -> sale.product.hasTag("Shipped", true) }
if(!shippedSales.isEmpty()){
    def shippedReportsData = report {
        keycode "ish.oncourse.slipDeliveryReport"
        records shippedSales
        generatePreview true
    }
    pdfs.add(shippedReportsData)
}

PdfUtil.mergePdfs(pdfs).toByteArray()


