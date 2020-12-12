package io.jenetics.jpx.format;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class TokenTest {

	@Test(dataProvider = "tokens")
	public void tokenize(final String pattern, final List<String> tokens) {
		final List<String> t = LocationFormatter.Builder.tokenize(pattern);
		assertEquals(t, tokens, String.format("%s != %s", t, tokens));
	}

	@DataProvider public Object[][] tokens() {
		return new Object[][] {
			{"LL", List.of("LL")},
			{".LL", List.of(".LL")},
			{"LL''", List.of("LL", "'", "'")},
			{"LL'''", List.of("LL", "'", "'", "'")},
			{"LL.LLL", List.of("LL.LLL")},
			{"+++LL[g''g]", List.of("+", "+", "+", "LL", "[", "g", "'", "'", "g", "]")},
			{"LL,LLL", List.of("LL,LLL")},
			{"LLDD", List.of("LL", "DD")},
			{"LL.LDD", List.of("LL.L", "DD")},
			{"LL.LDD.DDD", List.of("LL.L", "DD.DDD")},
			{"LL.L123DD.DDD", List.of("LL.L", "123", "DD.DDD")},
			{"LL.L123DD.DDD4567", List.of("LL.L", "123", "DD.DDD", "4567")},
			{"+LL.LDD.DDD", List.of("+", "LL.L", "DD.DDD")},
			{"+LL.LDD.DDDx", List.of("+", "LL.L", "DD.DDD", "x")},
			{"+LL.LDD.DDD''x", List.of("+", "LL.L", "DD.DDD", "'", "'", "x")},
			{"+LL.LDD.DDD'x'", List.of("+", "LL.L", "DD.DDD", "'", "x", "'")},
			{"+LL.LDD.DDD[x]ss", List.of("+", "LL.L", "DD.DDD", "[", "x", "]", "ss")},
			{"+LL.LDD.DDD[x]'ss", List.of("+", "LL.L", "DD.DDD", "[", "x", "]", "'", "ss")},
			{"+DD.DD[SSS]'XXX'sss.smm", List.of("+", "DD.DD", "[", "SSS", "]", "'", "XXX", "'", "sss.s", "mm")},
			{"+DD.DD[SSS]'XXXsss.smm", List.of("+", "DD.DD", "[", "SSS", "]", "'", "XXXsss.smm")}
		};
	}

}
