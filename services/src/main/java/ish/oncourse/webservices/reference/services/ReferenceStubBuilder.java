package ish.oncourse.webservices.reference.services;

import ish.oncourse.model.*;
import ish.oncourse.model.Module;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.util.GenericReferenceStub;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.cayenne.Persistent;

import java.util.HashMap;
import java.util.Map;

public class ReferenceStubBuilder {

	private Map<String, IReferenceStubBuilder> buildersv7 = new HashMap<>();

	public ReferenceStubBuilder() {
		buildersv7.put(getClassName(Country.class), new ish.oncourse.webservices.reference.v7.builders.CountryStubBuilder());
		buildersv7.put(getClassName(Language.class), new ish.oncourse.webservices.reference.v7.builders.LanguageStubBuilder());
		buildersv7.put(getClassName(Module.class), new ish.oncourse.webservices.reference.v7.builders.ModuleStubBuilder());
		buildersv7.put(getClassName(Qualification.class), new ish.oncourse.webservices.reference.v7.builders.QualificationStubBuilder());
		buildersv7.put(getClassName(TrainingPackage.class), new ish.oncourse.webservices.reference.v7.builders.TrainingPackageStubBuilder());
		buildersv7.put(getClassName(PostcodeDb.class), new ish.oncourse.webservices.reference.v7.builders.PostcodeStubBuilder());
	}

	public GenericReferenceStub convert(Persistent record,  final SupportedVersions version) {
		String key = record.getObjectId().getEntityName();
		IReferenceStubBuilder builder;
		
		switch (version) {
			case V7:
				builder = buildersv7.get(key);
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