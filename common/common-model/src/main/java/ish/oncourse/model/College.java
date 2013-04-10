package ish.oncourse.model;

import ish.oncourse.model.auto._College;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class College extends _College {
	private static final long serialVersionUID = 8618498696778850266L;
	public static final String REQUESTING_COLLEGE_ATTRIBUTE = "RequestingCollege";
	public static final String UNDEFINED_ANGEL_VERSION = "UNDEFINED";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public Set<String> getCollegeSiteStates() {
		Set<String> states = new HashSet<String>();

		for (Site site : getSites()) {
			if (site.getState() != null) {
				states.add(site.getState());
			}
		}

		return states;
	}

	public List<ConcessionType> getActiveConcessionTypes() {
		List<ConcessionType> activeConcessionTypes = new ArrayList<ConcessionType>();
		for (ConcessionType concessionType : getConcessionTypes()) {
			if (Boolean.TRUE.equals(concessionType.getIsConcession()) && Boolean.TRUE.equals(concessionType.getIsEnabled())) {
				activeConcessionTypes.add(concessionType);
			}
		}
		return activeConcessionTypes;
	}

	/**
	 * Returns college sites which marked as web visible.
	 * @return list of sites
	 */
	@SuppressWarnings("unchecked")
	public List<Site> getWebVisibleSites() {
		Expression expr = ExpressionFactory.matchExp(Site.COLLEGE_PROPERTY, this);
		expr = expr.andExp(ExpressionFactory.matchExp(Site.IS_WEB_VISIBLE_PROPERTY, true));
		SelectQuery q = new SelectQuery(Site.class, expr);
		Ordering order=new Ordering(Site.NAME_PROPERTY, SortOrder.ASCENDING);
		q.addOrdering(order);
		return getObjectContext().performQuery(q);
	}
}
