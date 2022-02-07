/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def newSale = record
if(newSale.product.hasTag("Shipped",true) || newSale.product.hasTag("Pickup",true)){
    newSale.addTag("Shipping Status/Waiting for Shipping")
}
newSale.context.commitChanges()