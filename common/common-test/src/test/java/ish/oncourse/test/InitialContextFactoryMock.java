package ish.oncourse.test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Mock for the {@link InitialContextFactory} that is used in tests.
 * 
 * @author ksenia
 * 
 */
public class InitialContextFactoryMock implements InitialContextFactory {

	/**
	 * The context instance to store jndi properties.
	 */
	private static Context context;

	/**
	 * Block which initializes the context.
	 */
	static {
		try {
			context = new InitialContext(true) {
				Map<String, Object> bindings = new HashMap<>();

				@Override
				public void bind(String name, Object obj) throws NamingException {
					bindings.put(name, obj);
				}

				@Override
				public Object lookup(String name) throws NamingException {
					return bindings.get(name);
				}
			};
		} catch (NamingException e) { // can't happen.
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the context. {@inheritDoc}
	 * 
	 * @see javax.naming.spi.InitialContextFactory#getInitialContext(java.util.Hashtable)
	 */
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		return context;
	}

	/**
	 * Binds the object "obj" to context with the name "name".
	 * 
	 * @param name
	 * @param obj
	 */
	public static void bind(String name, Object obj) {
		try {
			context.bind(name, obj);
		} catch (NamingException e) { // can't happen.
			throw new RuntimeException(e);
		}
	}
}