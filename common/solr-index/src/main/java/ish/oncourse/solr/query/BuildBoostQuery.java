/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.query;

import ish.oncourse.services.courseclass.ClassAge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.function.Function;

/**
 * User: akoiro
 * Date: 13/6/18
 */
public class BuildBoostQuery {
	private final static Logger LOGGER = LogManager.getLogger();

	private final static Function<String, String> BoostFunction = (String startDateFunction) -> String.format("recip(max(%s,0),1.15e-8,500,500)", startDateFunction);

	private final static String StartDateFunction = "if(lt(ms(startDate), $hideAge ), sub($startDatePlus100Year,ms(NOW-1YEAR/DAY)), $yearAge)";


	private final static Function<Integer, String> HideAge = (Integer hideAge) -> String.format("ms(NOW%sDAYS)", new DecimalFormat("+#;-#").format(hideAge));
	private final static String StartDatePlus100Year = "sum(ms(startDate, NOW),ms(NOW+100YEAR))";
	private final static String YearAge = "ms(startDate, NOW-1YEAR/DAY)";
	private final static String Query = "{!boost b=$boostFunction v=$mainQuery}";


	private int hideAge = 0;
	private String mainQuery = "*:*";


	public BuildBoostQuery hideAge(ClassAge classAge) {
		if (classAge != null) {
			switch (classAge.getType()) {
				case afterClassStarts:
					this.hideAge = -classAge.getDays();
					break;
				case beforeClassStarts:
					this.hideAge = classAge.getDays();
					break;
				default:
					this.hideAge = 0;
			}
		}
		return this;
	}

	public BuildBoostQuery mainQuery(String mainQuery) {
		this.mainQuery = mainQuery;
		return this;
	}

	public SolrQuery build() {
		SolrQuery q = new SolrQuery();
		q.setQuery(Query)
				.setParam(Param.mainQuery.name(), mainQuery)
				.setParam(Param.yearAge.name(), YearAge)
				.setParam(Param.boostFunction.name(), BoostFunction.apply(hideAge == 0 ? "$yearAge" : "$startDateFunction"));
		if (hideAge != 0)
			q.setParam(Param.startDateFunction.name(), StartDateFunction)
					.setParam(Param.hideAge.name(), HideAge.apply(hideAge))
					.setParam(Param.startDatePlus100Year.name(), StartDatePlus100Year);

		try {
			LOGGER.debug("Local Params: {}", URLDecoder.decode(q.toLocalParamsString(), "UTF-8"));
			LOGGER.debug("Result query: {}", URLDecoder.decode(q.toQueryString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn(e);
		}
		return q;
	}

	public enum Param {
		boostFunction,
		startDateFunction,
		hideAge,
		yearAge,
		startDatePlus100Year,
		mainQuery,
	}
}
