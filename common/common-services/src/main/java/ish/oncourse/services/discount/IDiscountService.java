package ish.oncourse.services.discount;

import ish.oncourse.model.Discount;

import java.util.List;

public interface IDiscountService {

	/**
	 * Retrieves the list of promotions(discounts with "promo-codes") stored in
	 * cookies.
	 * 
	 * @return
	 */
	List<Discount> getPromotions();

	/**
	 * Adds new promotion to the promotions list in cookies.
	 * 
	 * @param promotion
	 *            the promotion to add.
	 */
	void addPromotion(Discount promotion);

	/**
	 * Retrieves the discounts with the given ids.
	 * 
	 * @param ids
	 *            the array of ids for search.
	 * @return list of discounts.
	 */
	List<Discount> loadByIds(Object... ids);

	/**
	 * Retrieves the discount with the given code.
	 * 
	 * @param code
	 *            the given code.
	 * @return the discount with the given code if it exists null otherwise.
	 */
	Discount getByCode(String code);
}
