# LoggerUtil

LoggerUtil: Dump objects as JSON-like string

* No dependencies.
* Modify codes as you like.
* This is dump ulitity. Not a JSON serializer.

## Sample Class:

```java
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
```


## Sample Code:

```
LoggerUtil.dump(new Hoge());
```


## Output:

```
{
	"tc1": {
		"f1": null,
		"f2": "hogehoge",
		"f3": false,
		"f4": true,
		"f5": [
			1,
			3,
			5,
			7
		],
		"f6": [
			"a",
			"b",
			"c",
			"d"
		],
		"f7": [
			"x",
			"y",
			"z"
		]
	},
	"tc2": {
		"f1": null,
		"f2": "hogehoge",
		"f3": false,
		"f4": true,
		"f5": [
			1,
			3,
			5,
			7
		],
		"f6": [
			"a",
			"b",
			"c",
			"d"
		],
		"f7": [
			"x",
			"y",
			"z"
		]
	}
}
```
