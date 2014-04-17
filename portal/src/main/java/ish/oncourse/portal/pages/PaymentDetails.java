package ish.oncourse.portal.pages;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: artem
 * Date: 11/4/13
 * Time: 9:23 AM
 */
public class PaymentDetails {

    @Property
    private PaymentIn payment;

    @Inject
    private IPortalService portalService;

    @InjectPage
    private PageNotFound pageNotFound;

    @Inject
    private Messages messages;

    Object onActivate(String id) {
        if (id != null && id.length() > 0 && id.matches("\\d+"))
        {
            long idLong = Long.parseLong(id);
            /**
             * We need to use not shared cayenne context to be sure that we get actual data
             * for all related objects of the course class (sessions, room, sites).
             * It is important when we define timezone for start and end time.
             */
            this.payment =  portalService.getPaymentInBy(idLong);
            return payment == null ? pageNotFound:null;
        } else {
            return pageNotFound;
        }

    }

    public String getDate()
    {
        Date created = payment.getCreated();
        /**
         * we added created != null condition to exlude NPE when some old payment has null value in create field.
         * If the field has null value we show "(not set)" string
         * TODO The condition should be deleted after 21309 will be closed
         */
        if (created != null)
        {
            return String.format("%s ", new SimpleDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy).format(created));
        }
        else
        {
            return messages.get(PortalUtils.MESSAGE_KEY_notSet);
        }
    }


    public String getPhoneNumber(){

        Contact contact = payment.getContact();

        if(contact.getMobilePhoneNumber()!=null)
            return contact.getMobilePhoneNumber();
        else if(contact.getHomePhoneNumber()!=null)
            return contact.getHomePhoneNumber();
        else if(contact.getBusinessPhoneNumber()!=null)
            return contact.getBusinessPhoneNumber();
        else if(contact.getFaxNumber()!=null)
            return contact.getFaxNumber();
        else
            return null;
    }

    public Format moneyFormat(Money money)
    {
        return FormatUtils.chooseMoneyFormat(money);
    }


}
