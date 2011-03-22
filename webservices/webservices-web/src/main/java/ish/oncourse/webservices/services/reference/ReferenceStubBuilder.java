package ish.oncourse.webservices.services.reference;

import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.webservices.builders.reference.CountryStubBuilder;
import ish.oncourse.webservices.builders.reference.IReferenceStubBuilder;
import ish.oncourse.webservices.builders.reference.LanguageStubBuilder;
import ish.oncourse.webservices.builders.reference.ModuleStubBuilder;
import ish.oncourse.webservices.builders.reference.QualificationStubBuilder;
import ish.oncourse.webservices.builders.reference.TrainingPackageStubBuilder;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;

import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.Persistent;

@SuppressWarnings("rawtypes")
public class ReferenceStubBuilder {

	private Map<String, IReferenceStubBuilder> builders = new HashMap<String, IReferenceStubBuilder>();

	public ReferenceStubBuilder() {
		builders.put(getClassName(Country.class), new CountryStubBuilder());
		builders.put(getClassName(Language.class), new LanguageStubBuilder());
		builders.put(getClassName(Module.class), new ModuleStubBuilder());
		builders.put(getClassName(Qualification.class), new QualificationStubBuilder());
		builders.put(getClassName(TrainingPackage.class), new TrainingPackageStubBuilder());
	}

	/**
	 * 
	 * 
	 * @param record
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ReferenceStub convert(Persistent record) {
		String key = record.getObjectId().getEntityName();
		IReferenceStubBuilder builder = builders.get(key);

		if (builder == null) {
			throw new BuilderNotFoundException("Builder not found during record conversion", key);
		}

		return builder.convert(record);
	}
	
	private static String getClassName(Class<?> clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}
}