package ish.oncourse.services.datalayer;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.util.HTMLUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import static ish.oncourse.services.datalayer.DataLayerFactory.*;

public class CourseClass2ProductConverter extends AbstractProductConverter<CourseClass> {
	protected ProductId getProductId() {
		ProductId productId = new ProductId();
		productId.id = getValue().getCourse().getCode();
		productId.name = getValue().getCourse().getName();
		productId.url = HTMLUtils.getCanonicalLinkPathFor(getValue().getCourse(), getProductFactory().getRequest());
		productId.sku = getValue().getUniqueIdentifier();
		return productId;
	}

	@Override
	protected ProductCategory getCategory() {
		Tag tag = getTag();
		ProductCategory category = new ProductCategory();
		category.type = CategoryType.CLASS.name().toLowerCase();
		category.primary = getCategoryBy(tag);
		return category;
	}

	private Tag getTag() {
		List<Tag> tagsForEntity = getProductFactory().getTagService().getTagsForEntity(Course.class.getSimpleName(),
				getValue().getCourse().getId());
		if (tagsForEntity.size() > 0)
			return tagsForEntity.get(0);
		else
			return null;
	}

	private String getCategoryBy(Tag tag) {
		if (tag == null)
			return StringUtils.EMPTY;
		else
			return tag.getName();
	}

	@Override
	protected Price getPrice() {
		Price price = new Price();
		price.base = getValue().getFeeExGst();
		price.withTax = getValue().getFeeIncGst();
		price.tax = getValue().getFeeGst();
		price.total = price.withTax;
		return price;
	}
}
