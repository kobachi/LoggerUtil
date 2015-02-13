import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logger support utility
 *
 * @author kobachi
 *
 */
public class LoggerUtil {

	/** End of line */
	private static final String EOL = System.lineSeparator();

	/** Getter method name pattern */
	private static final Pattern GETTER_PATTERN = Pattern.compile("^(get|is)([A-Z]+)(.+)$");

	/** Simple Key-Value pair value class */
	private static class KV {
		public final Object key;
		public final Object value;
		public KV(final Object key, final Object value) {
			this.key = key;
			this.value = value;
		}
	}


	/**
	 * Stringify object's fields as JSON-like description
	 * @param o
	 * @return
	 */
	public static String dump(final Object o) {
		return dump(o, false);
	}


	/**
	 * Stringify object's fields as JSON-like description
	 * @param o
	 * @param debug
	 * @return
	 */
	public static String dump(final Object o, final boolean debug) {
		return dump(o, "\t", 1, debug);
	}


	/**
	 * Stringify object's fields as JSON-like description
	 * @param o
	 * @param indentStr
	 * @param indent
	 * @param debug
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String dump(final Object o, final String indentStr, final int indent, final boolean debug) {
		if (o == null) {
			return "null";
		}
		final Class c = o.getClass();
		if (c.isArray() || Iterable.class.isAssignableFrom(c)) {
			//array or iterable
			Object[] oa = null;
			if (c.isArray()) {
				oa = (Object[])Array.newInstance(wrap(c.getComponentType()), Array.getLength(o));
				for (int i = 0; i < oa.length; i++) {
					oa[i] = Array.get(o, i);
				}
			}
			else if (Iterable.class.isAssignableFrom(c)) {
				final Iterator ite = Iterable.class.cast(o).iterator();
				final List<Object> _oa = new ArrayList<Object>();
				while (ite.hasNext()) {
					_oa.add(ite.next());
				}
				oa = _oa.toArray(new Object[_oa.size()]);
			}
			final StringBuilder sb = new StringBuilder();
			sb.append("[");
			if (debug) sb.append(EOL);
			final String prefix = debug ? repeat(indentStr, indent) : "";
			final List<String> itemList = new ArrayList<String>();
			for (final Object oo : oa) {
				itemList.add(prefix + dump(oo, indentStr, indent + 1, debug));
			}
			joinTo(sb, "," + (debug ? EOL : ""), itemList);
			if (debug) sb.append(EOL).append(repeat(indentStr, indent - 1));
			sb.append("]");
			return sb.toString();
		}
		if (Map.class.isAssignableFrom(c)) {
			//Map
			final Map<?, ?> m = (Map)o;
			final List<KV> kvList = new ArrayList<KV>();
			for (final Entry<?, ?> e : m.entrySet()) {
				kvList.add(new KV(e.getKey(), e.getValue()));
			}
			return dump(kvList, indentStr, indent + 1, debug);
		}
		if (c.isPrimitive()) {
			//boolean, byte, char, short, int, long, float
			return String.valueOf(c);
		}
		if (Boolean.class.equals(c) || Byte.class.equals(c) || Character.class.equals(c) || Short.class.equals(c)
			|| Integer.class.equals(c) || Long.class.equals(c) || Float.class.equals(c)) {
			//Wrapped boolean, byte, char, short, int, long, float
			return o.toString();
		}
		final Map<String, KV> fieldMap = new HashMap<String, KV>();
		try {
			for (final Method method : c.getMethods()) {
				if (Object.class.equals(method.getDeclaringClass())) {
					continue;
				}
				final int mod = method.getModifiers();
				if (Modifier.isPublic(mod) == false || Modifier.isStatic(mod)) {
					continue;
				}
				if (method.getParameterTypes().length != 0) {
					continue;
				}
				if ("toString".equals(method.getName())) {
					final StringBuilder sb = new StringBuilder();
					sb.append("\"");
					sb.append(quote(o.toString()));
					sb.append("\"");
					return sb.toString();
				}
				final Matcher matcher = GETTER_PATTERN.matcher(method.getName());
				if (matcher.matches() == false) {
					continue;
				}
				final String propertyName = matcher.group(2).toLowerCase() + matcher.group(3);
				try {
					fieldMap.put(
							propertyName,
							new KV(method.getReturnType().getSimpleName() + " " + method.getName() + "()", dump(method.invoke(o),
									indentStr, indent + 1, debug)));
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					//fail
				}
			}
			for (final Field field : c.getFields()) {
				final int mod = field.getModifiers();
				if (Modifier.isPublic(mod) == false || Modifier.isStatic(mod)) {
					continue;
				}
				if (fieldMap.containsKey(field.getName())) {
					continue;
				}
				try {
					fieldMap.put(field.getName(), new KV(field.getName(), dump(field.get(o), indentStr, indent + 1, debug)));
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					//fail
				}
			}
		}
		catch (final SecurityException e) {
			//fail
		}
		final String prefix = debug ? repeat(indentStr, indent) : "";
		final List<String> fields = new ArrayList<String>();
		final List<String> fieldNameList = new ArrayList<String>(fieldMap.keySet());
		Collections.sort(fieldNameList);
		for (final String fieldName : fieldNameList) {
			final StringBuilder sb = new StringBuilder();
			sb.append(prefix).append("\"").append(quote(fieldName)).append("\":");
			if (debug) sb.append(" ");
			sb.append(fieldMap.get(fieldName).value);
			fields.add(sb.toString());
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		if (debug) sb.append(EOL);
		joinTo(sb, "," + (debug ? EOL : ""), fields);
		if (debug) sb.append(EOL).append(repeat(indentStr, indent - 1));
		sb.append("}");
		return sb.toString();
	}


	/**
	 * Quotes string
	 * @param str
	 * @return
	 */
	private static String quote(final String str) {
		return str.replace("\\", "\\\\").replace("\"", "\\\"");
	}


	/**
	 * Wrap primitive classes
	 * @param c
	 * @return
	 */
	static Class wrap(final Class<?> c) {
		if (boolean.class.equals(c)) return Boolean.class;
		if (byte.class.equals(c)) return Byte.class;
		if (char.class.equals(c)) return Character.class;
		if (double.class.equals(c)) return Double.class;
		if (float.class.equals(c)) return Float.class;
		if (int.class.equals(c)) return Integer.class;
		if (long.class.equals(c)) return Long.class;
		if (short.class.equals(c)) return Short.class;
		if (void.class.equals(c)) return Void.class;
		return c;
	}


	/**
	 * Join string list with delimiter and append to string builder
	 * @param sb
	 * @param delimiter
	 * @param items
	 */
	private static void joinTo(final StringBuilder sb, final String delimiter, final List<String> items) {
		final int end = items.size() - 1;
		for (int i = 0; i < end; i++) {
			sb.append(items.get(i));
			sb.append(delimiter);
		}
		sb.append(items.get(items.size() - 1));
	}


	/**
	 * Returns repeated string
	 * @param str
	 * @param count
	 * @return
	 */
	private static String repeat(final String str, final int count) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

}