package ish.oncourse.portal.components;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.FieldValidationSupport;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.Renderable;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.apache.tapestry5.services.Request;

public class CheckBoxList extends AbstractField {

	/**
	 * Model used to define the values and labels used when rendering the checklist.
	 */
	@Parameter(required = true)
	private SelectModel model;

	/**
	 * The list of selected values from the {@link org.apache.tapestry5.SelectModel}. This will be updated when the form is submitted. If
	 * the value for the parameter is null, a new list will be created, otherwise the existing list will be cleared. If unbound, defaults to
	 * a property of the container matching this component's id.
	 */
	@Parameter(required = true, autoconnect = true)
	private List<Object> selected;

	/**
	 * Encoder used to translate between server-side objects and client-side strings.
	 */
	@Parameter(required = true, allowNull = false)
	private ValueEncoder<Object> encoder;

	/**
	 * The object that will perform input validation. The validate binding prefix is generally used to provide this object in a declarative
	 * fashion.
	 */
	@Parameter(defaultPrefix = BindingConstants.VALIDATE)
	@SuppressWarnings("unchecked")
	private FieldValidator<Object> validate;

	@Inject
	private Request request;

	@Inject
	private FieldValidationSupport fieldValidationSupport;

	@Environmental
	private ValidationTracker tracker;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private ComponentDefaultProvider defaultProvider;

	@Property
	private List<Renderable> availableOptions;

	private MarkupWriter markupWriter;

	private final class RenderCheckBox implements Renderable {
		private final OptionModel model;

		private RenderCheckBox(final OptionModel model) {
			this.model = model;
		}

		public void render(MarkupWriter writer) {
			final String clientValue = encoder.toClient(model.getValue());

			writer.element("label");
			writer.attributes("for", clientValue);
			writer.write(model.getLabel());
			writer.end();

			final Element checkbox = writer.element("input", "type", "checkbox", "name", getControlName(), "value", clientValue);
			
			writer.attributes("id", clientValue);
			
			if (getSelected().contains(model.getValue())) {
				checkbox.attribute("checked", "checked");
			}

			writer.end();
		}
	}

	void setupRender(final MarkupWriter writer) {
		markupWriter = writer;

		availableOptions = CollectionFactory.newList();

		final SelectModelVisitor visitor = new SelectModelVisitor() {
			public void beginOptionGroup(final OptionGroupModel groupModel) {
			}

			public void option(final OptionModel optionModel) {
				availableOptions.add(new RenderCheckBox(optionModel));
			}

			public void endOptionGroup(final OptionGroupModel groupModel) {
			}

		};

		model.visit(visitor);
	}

	@Override
	protected void processSubmission(final String elementName) {

		final String[] parameters = request.getParameters(elementName);

		List<Object> selected = this.selected;

		if (selected == null) {
			selected = CollectionFactory.newList();
		} else {
			selected.clear();
		}

		if (parameters != null) {
			for (final String value : parameters) {
				final Object objectValue = encoder.toValue(value);

				selected.add(objectValue);
			}

		}

		try {
			this.fieldValidationSupport.validate(selected, this.componentResources, this.validate);

			this.selected = selected;
		} catch (final ValidationException e) {
			this.tracker.recordError(this, e.getMessage());
		}
	}

	Set<Object> getSelected() {
		if (selected == null) {
			return Collections.emptySet();
		}

		return CollectionFactory.newSet(selected);
	}

	/**
	 * Computes a default value for the "validate" parameter using {@link org.apache.tapestry5.services.FieldValidatorDefaultSource}.
	 */

	Binding defaultValidate() {
		return this.defaultProvider.defaultValidatorBinding("selected", this.componentResources);
	}

	@Override
	public boolean isRequired() {
		return validate.isRequired();
	}

}
