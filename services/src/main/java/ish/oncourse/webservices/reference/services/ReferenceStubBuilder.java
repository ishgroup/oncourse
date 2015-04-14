package ish.oncourse.webservices.reference.services;

import ish.oncourse.model.*;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.reference.builders.*;
import ish.oncourse.webservices.util.GenericReferenceStub;
import org.apache.cayenne.Persistent;

import java.util.HashMap;
import java.util.Map;

public class ReferenceStubBuilder {

	private Map<String, IReferenceStubBuilder> builders = new HashMap<>();

	public ReferenceStubBuilder() {
		builders.put(getClassName(Country.class), new CountryStubBuilder());
		builders.put(getClassName(Language.class), new LanguageStubBuilder());
		builders.put(getClassName(Module.class), new ModuleStubBuilder());
		builders.put(getClassName(Qualification.class), new QualificationStubBuilder());
		builders.put(getClassName(TrainingPackage.class), new TrainingPackageStubBuilder());
	}

	public GenericReferenceStub convert(Persistent record) {
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