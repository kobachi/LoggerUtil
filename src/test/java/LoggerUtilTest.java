import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;



public class LoggerUtilTest {

	static class TestClass {

		public String f1;
		private String f2 = "hogehoge";
		public String getF2() {
			return f2;
		}
		public boolean f3;
		public boolean isF4() {
			return true;
		}
		public static String getX() {
			return "fail";
		}
		public static boolean isY() {
			return false;
		}
		public int[] f5 = new int[] {
				1, 3, 5, 7
		};
		public List<String> f6 = Arrays.asList("a", "b", "c", "d");
		public String[] getF7() {
			return new String[] {
					"x", "y", "z"
			};
		}
	}

	static class Hoge {
		public TestClass tc1 = new TestClass();
		public TestClass tc2 = new TestClass();
	}

	@Test
	public void testDump() {
		assertEquals(
				"{\"tc1\":{\"f1\":null,\"f2\":\"hogehoge\",\"f3\":false,\"f4\":true,\"f5\":[1,3,5,7],\"f6\":[\"a\",\"b\",\"c\",\"d\"],\"f7\":[\"x\",\"y\",\"z\"]},\"tc2\":{\"f1\":null,\"f2\":\"hogehoge\",\"f3\":false,\"f4\":true,\"f5\":[1,3,5,7],\"f6\":[\"a\",\"b\",\"c\",\"d\"],\"f7\":[\"x\",\"y\",\"z\"]}}",
				LoggerUtil.dump(new Hoge(), false));
	}


	@Test
	public void testDumpArray() {
		assertEquals(
				"[{\"f1\":null,\"f2\":\"hogehoge\",\"f3\":false,\"f4\":true,\"f5\":[1,3,5,7],\"f6\":[\"a\",\"b\",\"c\",\"d\"],\"f7\":[\"x\",\"y\",\"z\"]},{\"f1\":null,\"f2\":\"hogehoge\",\"f3\":false,\"f4\":true,\"f5\":[1,3,5,7],\"f6\":[\"a\",\"b\",\"c\",\"d\"],\"f7\":[\"x\",\"y\",\"z\"]}]",
				LoggerUtil.dump(new TestClass[] {
						new TestClass(), new TestClass()
				}, false)
		);
	}

	@Test
	public void testDumpList() {
		assertEquals(
				"[{\"f1\":null,\"f2\":\"hogehoge\",\"f3\":false,\"f4\":true,\"f5\":[1,3,5,7],\"f6\":[\"a\",\"b\",\"c\",\"d\"],\"f7\":[\"x\",\"y\",\"z\"]},{\"f1\":null,\"f2\":\"hogehoge\",\"f3\":false,\"f4\":true,\"f5\":[1,3,5,7],\"f6\":[\"a\",\"b\",\"c\",\"d\"],\"f7\":[\"x\",\"y\",\"z\"]}]",
				LoggerUtil.dump(Arrays.asList(new TestClass(), new TestClass()), false)
		);
	}


	@Test
	public void testDumpMap() {
		Map<String, TestClass> m = new HashMap<String, TestClass>();
		m.put("a", new TestClass());
		m.put("b", new TestClass());
		assertEquals(
				"[{\"key\":\"b\",\"value\":{\"f1\":null,\"f2\":\"hogehoge\",\"f3\":false,\"f4\":true,\"f5\":[1,3,5,7],\"f6\":[\"a\",\"b\",\"c\",\"d\"],\"f7\":[\"x\",\"y\",\"z\"]}},{\"key\":\"a\",\"value\":{\"f1\":null,\"f2\":\"hogehoge\",\"f3\":false,\"f4\":true,\"f5\":[1,3,5,7],\"f6\":[\"a\",\"b\",\"c\",\"d\"],\"f7\":[\"x\",\"y\",\"z\"]}}]",
				LoggerUtil.dump(m, false));
	}

	@Test
	public void testDumpWithDebug(){
		String eol = System.lineSeparator();
		assertEquals("{" + eol + 
				"	\"tc1\": {" + eol + 
				"		\"f1\": null," + eol + 
				"		\"f2\": \"hogehoge\"," + eol + 
				"		\"f3\": false," + eol + 
				"		\"f4\": true," + eol + 
				"		\"f5\": [" + eol + 
				"			1," + eol + 
				"			3," + eol + 
				"			5," + eol + 
				"			7" + eol + 
				"		]," + eol + 
				"		\"f6\": [" + eol + 
				"			\"a\"," + eol + 
				"			\"b\"," + eol + 
				"			\"c\"," + eol + 
				"			\"d\"" + eol + 
				"		]," + eol + 
				"		\"f7\": [" + eol + 
				"			\"x\"," + eol + 
				"			\"y\"," + eol + 
				"			\"z\"" + eol + 
				"		]" + eol + 
				"	}," + eol + 
				"	\"tc2\": {" + eol + 
				"		\"f1\": null," + eol + 
				"		\"f2\": \"hogehoge\"," + eol + 
				"		\"f3\": false," + eol + 
				"		\"f4\": true," + eol + 
				"		\"f5\": [" + eol + 
				"			1," + eol + 
				"			3," + eol + 
				"			5," + eol + 
				"			7" + eol + 
				"		]," + eol + 
				"		\"f6\": [" + eol + 
				"			\"a\"," + eol + 
				"			\"b\"," + eol + 
				"			\"c\"," + eol + 
				"			\"d\"" + eol + 
				"		]," + eol + 
				"		\"f7\": [" + eol + 
				"			\"x\"," + eol + 
				"			\"y\"," + eol + 
				"			\"z\"" + eol + 
				"		]" + eol + 
				"	}" + eol + 
				"}",
				LoggerUtil.dump(new Hoge(), true));
	}
}
