package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.v6.stubs.reference.ReferenceStub;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTransportTest {
    static
    {
        System.getProperties().put("org.apache.cxf.stax.allowInsecureParser","true");
    }

	public static final String PACKAGE_NAME_REFERENCE_STUBS = "ish.oncourse.webservices.v4.stubs.reference";

	private static List<String> getStubClassNamesBy(String packageName, @SuppressWarnings("rawtypes") Class filterClass)
	{
		BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
		ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);

		TypeFilter tf = new AssignableTypeFilter(filterClass);
		s.addIncludeFilter(tf);
		s.scan(packageName);

		String[] beanNames = bdr.getBeanDefinitionNames();

		ArrayList<String> result = new ArrayList<>();

		for (String beanName : beanNames) {

			String className = org.apache.commons.lang.StringUtils.capitalize(beanName);
			if (className.endsWith("Stub") && !className.equals("HollowStub")) {
				result.add(className);
			}
		}
		return result;
	}

	public static List<String> getReferenceStubBeanNames() {
		return getStubClassNamesBy(PACKAGE_NAME_REFERENCE_STUBS, ReferenceStub.class);
	}

	public static <T> ArrayList<T> createStubsBy(List<String> stubClassNames,String packageName,Class<T> parentStubClass) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		ArrayList<T> stubs = new ArrayList<>();
		for (String stubClassName : stubClassNames) {
			@SuppressWarnings("rawtypes")
			Class aClass = Class.forName(String.format("%s.%s", packageName, stubClassName));
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Constructor constructor = aClass.getConstructor();
			@SuppressWarnings("unchecked")
			T replicationStub = (T) constructor.newInstance();
			stubs.add(replicationStub);
		}
		return stubs;
	}

}
