package ish.oncourse.webservices.reference.services;

import ish.oncourse.model.*;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.util.GenericReferenceStub;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.cayenne.Persistent;

import java.util.HashMap;
import java.util.Map;

public class ReferenceStubBuilder {

	private Map<String, IReferenceStubBuilder> buildersv4 = new HashMap<>();
	private Map<String, IReferenceStubBuilder> buildersv5 = new HashMap<>();

	public ReferenceStubBuilder() {
		buildersv4.put(getClassName(Country.class), new ish.oncourse.webservices.reference.v4.builders.CountryStubBuilder());
		buildersv4.put(getClassName(Language.class), new ish.oncourse.webservices.reference.v4.builders.LanguageStubBuilder());
		buildersv4.put(getClassName(Module.class), new ish.oncourse.webservices.reference.v4.builders.ModuleStubBuilder());
		buildersv4.put(getClassName(Qualification.class), new ish.oncourse.webservices.reference.v4.builders.QualificationStubBuilder());
		buildersv4.put(getClassName(TrainingPackage.class), new ish.oncourse.webservices.reference.v4.builders.TrainingPackageStubBuilder());

		buildersv5.put(getClassName(Country.class), new ish.oncourse.webservices.reference.v5.builders.CountryStubBuilder());
		buildersv5.put(getClassName(Language.class), new ish.oncourse.webservices.reference.v5.builders.LanguageStubBuilder());
		buildersv5.put(getClassName(Module.class), new ish.oncourse.webservices.reference.v5.builders.ModuleStubBuilder());
		buildersv5.put(getClassName(Qualification.class), new ish.oncourse.webservices.reference.v5.builders.QualificationStubBuilder());
		buildersv5.put(getClassName(TrainingPackage.class), new ish.oncourse.webservices.reference.v5.builders.TrainingPackageStubBuilder());
	}

	public GenericReferenceStub convert(Persistent record,  final SupportedVersions version) {
		String key = record.getObjectId().getEntityName();
		IReferenceStubBuilder builder;
		
		switch (version) {
			case V4:
				builder = buildersv4.get(key);
				break;
			case V5:
				builder = buildersv5.get(key);
				break;
			default:
				builder = null;
				break;
		}
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