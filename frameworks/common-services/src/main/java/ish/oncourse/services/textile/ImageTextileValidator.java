package ish.oncourse.services.textile;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.util.ValidationErrors;

public class ImageTextileValidator implements IValidator {

	private static final String IMAGE_NAME_REGEXP = "\\{image:name=(\"|&#8220;)(\\w+)(\"|&#8220;)\\}";
	private static final String IMAGE_ID_REGEXP = "\\{image:id=(\"|&#8220;)(\\d+)(\"|&#8220;)\\}";
	private static final String QUOT_REGEXP = "\"|&#8220;";

	public void validate(String tag, ValidationErrors errors, ObjectContext context, College currentCollege) {
		tag = tag.replaceAll(" ", "");
		Expression qualifier;
		if(tag.matches(IMAGE_ID_REGEXP)){
			String id=getValueInFirstQuots(tag);
			qualifier = ExpressionFactory.matchExp(BinaryInfo.COLLEGE_PROPERTY, currentCollege)
			.andExp(ExpressionFactory.matchExp(BinaryInfo.ID_PK_COLUMN, Long.valueOf(id)));
			checkIfImageExists(context, qualifier, errors, "There's no image with the id: "+id);
		}else if(tag.matches(IMAGE_NAME_REGEXP)){
			String name=getValueInFirstQuots(tag);
			qualifier=ExpressionFactory.matchExp(BinaryInfo.COLLEGE_PROPERTY, currentCollege)
			.andExp(ExpressionFactory.matchExp(BinaryInfo.NAME_PROPERTY, name));
			checkIfImageExists(context, qualifier, errors, "There's no image with the name: "+name);
		}else{
			errors.addFailure("The image tag '"+tag+"' doesn't match nor {image:id=\"id\"}, nor {image:name=\"name\"}");
		}
	}

	/**
	 * @param tag
	 * @return
	 */
	private String getValueInFirstQuots(String tag) {
		return tag.split(QUOT_REGEXP)[1];
	}

	private void checkIfImageExists(ObjectContext context, Expression qualifier, ValidationErrors errors, String errorMessage){
		SelectQuery query =new SelectQuery(BinaryInfo.class);
		query.andQualifier(qualifier);
		List<BinaryInfo> listResult = context.performQuery(query);
		if(listResult==null||listResult.isEmpty()){
			errors.addFailure(errorMessage);
		}
	}
}
