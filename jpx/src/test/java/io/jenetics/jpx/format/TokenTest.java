/*
 * Java GPX Library (@__identifier__@).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jenetics.jpx.format;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TokenTest {

	@Test(dataProvider = "tokens")
	public void tokenize(final String pattern, final List<String> tokens) {
		final List<String> t = LocationFormatter.Builder.tokenize(pattern);
		assertEquals(t, tokens, String.format("%s != %s", t, tokens));
	}

	@DataProvider
	public Object[][] tokens() {
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
