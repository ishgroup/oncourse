package ish.oncourse.services.discount;

import ish.oncourse.model.Discount;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

public class DiscountService implements IDiscountService {

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#getPromotions()
	 */
	public List<Discount> getPromotions() {
		List<Long> discountIds = cookiesService.getCookieCollectionValue(Discount.PROMOTIONS_KEY, Long.class);
		if (discountIds == null) {
			return new ArrayList<>();
		}
		final List<Discount> result = ExpressionFactory.noMatchExp(Discount.CODE_PROPERTY, null).filterObjects(loadByIds(discountIds.toArray()));
		return result.isEmpty() ? new ArrayList<Discount>(0): result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#addPromotion(ish.oncourse.model.Discount)
	 */
	@Override
	public void addPromotion(Discount promotion) {
		if (promotion != null) {
			cookiesService.appendValueToCookieCollection(Discount.PROMOTIONS_KEY, promotion.getId().toString());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#loadByIds(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Discount> loadByIds(Object... ids) {
		if (ids == null || ids.length == 0) {
			return new ArrayList<>();
		}

		SelectQuery q = new SelectQuery(Discount.class);
		q.andQualifier(ExpressionFactory.inDbExp(Discount.ID_PK_COLUMN, ids)
				.andExp(getCurrentCollegeFilter())
				.andExp(Discount.getCurrentDateFilter())
				.andExp(getAvailableOnWebFilter()));

		return cayenneService.sharedContext().performQuery(q);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#getByCode(java.lang.String)
	 */
	@Override
	public Discount getByCode(String code) {
		if (StringUtils.trimToNull(code) == null) {
			return null;
		}
		Expression qualifier = ExpressionFactory.matchExp(Discount.CODE_PROPERTY, code).andExp(
				Discount.getCurrentDateFilter()).andExp(getCurrentCollegeFilter()).andExp(getAvailableOnWebFilter());
		SelectQuery query = new SelectQuery(Discount.class, qualifier);
		@SuppressWarnings("unchecked")
		List<Discount> result = cayenneService.sharedContext().performQuery(query);
		return result.isEmpty() ? null : result.get(0);
	}

	private Expression getCurrentCollegeFilter() {
		return ExpressionFactory.matchExp(Discount.COLLEGE_PROPERTY, webSiteService.getCurrentCollege());
	}
	
	private Expression getAvailableOnWebFilter() {
		return ExpressionFactory.matchExp(Discount.IS_AVAILABLE_ON_WEB_PROPERTY, true);
	}
}

