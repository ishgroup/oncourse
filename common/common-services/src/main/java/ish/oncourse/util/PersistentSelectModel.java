package ish.oncourse.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.reflect.ClassDescriptor;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

// TODO: something to contribute to "tapestry5-cayenne"?
public class PersistentSelectModel extends AbstractSelectModel {

	private final List<OptionModel> options;

	public PersistentSelectModel(Collection<? extends Persistent> objects,
			String labelKey, String valueKey) {

		int size = objects.size();

		options = new ArrayList<>(size);

		if (size > 0) {

			EntityResolver resolver = objects.iterator().next()
					.getObjectContext().getEntityResolver();

			for (Persistent object : objects) {

				ClassDescriptor descriptor = resolver.getClassDescriptor(object
						.getObjectId().getEntityName());

				Object label = descriptor.getDeclaredProperty(labelKey)
						.readProperty(object);
				Object value = descriptor.getDeclaredProperty(valueKey)
						.readProperty(object);

				options.add(new OptionModelImpl(String.valueOf(value), String
						.valueOf(label)));
			}
		}
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}

}
