package ish.oncourse.services.textile;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public class TextileUtil {

	public static final String IMAGE_NAME_REGEXP = "\\{image:name=(\"|&#8220;)(\\w+)(\"|&#8220;)}";
	public static final String IMAGE_ID_REGEXP = "\\{image:id=(\"|&#8220;)(\\d+)(\"|&#8220;)}";
	public static final String QUOT_REGEXP = "\"|&#8220;";

	/**
	 * @param tag
	 * @return
	 */
	public static String getValueInFirstQuots(String tag) {
		return tag.split(QUOT_REGEXP)[1];
	}

	public static BinaryInfo getImageBinaryInfo(ObjectContext context,
			String searchProperty, Object value, College college) {

		Expression qualifier = ExpressionFactory.matchExp(
				BinaryInfo.COLLEGE_PROPERTY, college);
		if (BinaryInfo.ID_PK_COLUMN.equals(searchProperty)) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					BinaryInfo.ID_PROPERTY, value));
		} else if (BinaryInfo.NAME_PROPERTY.equals(searchProperty)) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					BinaryInfo.NAME_PROPERTY, value));
		}
		SelectQuery query = new SelectQuery(BinaryInfo.class, qualifier);
		List<BinaryInfo> listResult = context.performQuery(query);
		return listResult != null && !listResult.isEmpty() ? listResult.get(0)
				: null;
	}
}
