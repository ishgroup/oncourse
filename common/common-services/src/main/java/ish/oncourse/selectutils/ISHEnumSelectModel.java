package ish.oncourse.selectutils;

import ish.common.util.DisplayableExtendedEnumeration;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.util.AbstractSelectModel;
import org.apache.tapestry5.util.EnumSelectModel;

import java.util.List;

/**
 * Customized Tapestry Select Model that processes enum values and displays the
 * proper label for {@link DisplayableExtendedEnumeration}.
 * see {@link EnumSelectModel}
 * 
 * @author ksenia
 * 
 */
public class ISHEnumSelectModel extends AbstractSelectModel {
	private final List<OptionModel> options = CollectionFactory.newList();

	@SuppressWarnings("rawtypes")
	public <T extends Enum> ISHEnumSelectModel(Class<T> enumClass, Messages messages) {
		this(enumClass, messages, enumClass.getEnumConstants());
	}

	@SuppressWarnings("rawtypes")
	public <T extends Enum> ISHEnumSelectModel(Class<T> enumClass, Messages messages, T[] values) {
		assert enumClass != null;
		assert messages != null;
		String prefix = enumClass.getSimpleName();

		for (T value : values) {
			String label = null;
			if (value instanceof DisplayableExtendedEnumeration) {
				label = ((DisplayableExtendedEnumeration) value).getDisplayName();
			}
			String name = value.name();

			String key = prefix + "." + name;

			if (label == null || "".equals(label) || messages.contains(key)
					|| messages.contains(name)) {
				label = TapestryInternalUtils.getLabelForEnum(messages, prefix, value);
			}
			options.add(new OptionModelImpl(label, value));
		}
	}

	/**
	 * Returns null.
	 */
	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	/**
	 * Returns the option groupos created in the constructor.
	 */
	public List<OptionModel> getOptions() {
		return options;
	}

}
