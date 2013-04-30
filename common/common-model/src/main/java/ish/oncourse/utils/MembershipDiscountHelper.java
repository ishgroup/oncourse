package ish.oncourse.utils;

import ish.oncourse.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author akoyro
 */
public class MembershipDiscountHelper {
    private Discount discount;
    private Contact contact;

    private HashMap<ContactRelationType,List<MembershipProduct>> relations = new HashMap<>();


    private List<MembershipProduct> getMembershipProductsBy(Contact contact) {
        List<MembershipProduct> membershipProducts = new ArrayList<>();
        List<Membership> memberships = contact.getMemberships();
        for (Membership membership : memberships)
            membershipProducts.add((MembershipProduct) membership.getProduct());
        return membershipProducts;
    }

    private List<MembershipProduct> getMembershipProductsBy(ContactRelationType contactRelationType)
    {
        List<MembershipProduct> result = relations.get(contactRelationType);
        if (result == null)
        {
            result = new ArrayList<>();
            relations.put(contactRelationType, result);
        }
        return result;
    }

    private void initRelations()
    {

        List<ContactRelation> contactRelations = contact.getToContacts();
        for (ContactRelation contactRelation : contactRelations) {
            List<MembershipProduct> list = getMembershipProductsBy(contactRelation.getRelationType());
            list.addAll(getMembershipProductsBy(contactRelation.getToContact()));
        }
        contactRelations = contact.getFromContacts();
        for (ContactRelation contactRelation : contactRelations) {
            List<MembershipProduct> list = getMembershipProductsBy(contactRelation.getRelationType());
            list.addAll(getMembershipProductsBy(contactRelation.getFromContact()));
        }
    }


    public boolean isEligibile() {

        List<DiscountMembership> discountMemberships = discount.getDiscountMembershipProducts();
        if (discountMemberships == null || discountMemberships.isEmpty())
            return true;

        for (DiscountMembership discountMembership : discountMemberships) {
            MembershipProduct product = discountMembership.getMembershipProduct();

            if (getMembershipProductsBy(contact).contains(product))
                return true;

            if (discountMembership.getApplyToMemberOnly())
                continue;

            initRelations();

            List<DiscountMembershipRelationType> types = discountMembership.getDiscountMembershipRelationTypes();
            for (DiscountMembershipRelationType relationType : types) {
                List<MembershipProduct> list = relations.get(relationType.getContactRelationType());
                if (list != null && list.contains(product))
                    return true;
            }
        }
        return false;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
