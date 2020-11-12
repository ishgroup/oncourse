/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

select i.contactId id, sum((il.priceEachexTax - il.discountEachexTax + il.taxEach) * il.quantity) amount
from Invoice i
inner join InvoiceLine il on il.invoiceId = i.id
where i.invoiceDate <= #bind($date) group by i.contactId;

select i.contactId id, sum(pl.amount) amount
from PaymentIn p
inner join PaymentInLine pl on p.id = pl.paymentInId and pl.amount != 0
inner join Invoice i on i.id = pl.invoiceId
where p.status = 3 and p.paymentDate <= #bind($date) group by i.contactId;

select i.contactId id, sum(pl.amount) amount
from PaymentOut p
inner join PaymentOutLine pl on p.id = pl.paymentOutId and pl.amount != 0
inner join Invoice i on i.id = pl.invoiceId
where p.status = 3  and p.paymentDate <= #bind($date) group by i.contactId;

select i.id contactId, (COALESCE(i.amount,0) - COALESCE(pin.amount,0) + COALESCE(pout.amount,0)) owing
from (%s) i
left join (%s) pin on i.id = pin.id
left join (%s) pout on i.id = pout.id;

select c.id as ID, f.owing as AMOUNTOWING from Contact c
inner join (%s) f on c.id = f.contactId
where f.owing != 0;
