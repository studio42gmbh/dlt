/*
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.dlt;

import de.s42.dlt.parser.DefaultTemplateContext;
import de.s42.dlt.parser.TemplateContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class ModifierTest
{

	@Test
	public void evaluateModifierExists() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:exists}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1}${test:exists}"), "true");
	}

	@Test
	public void evaluateModifierLength() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:length}"), "0");
		TemplateContext context = new DefaultTemplateContext();
		context.setBinding("list", new Object[]{"A", "B", "C", "D"});
		Assert.assertEquals(DLT.evaluate("${list:length}", context), "4");
	}

	@Test
	public void evaluateModifierGreater0() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:greater0}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1}${test:greater0}"), "true");
		Assert.assertEquals(DLT.evaluate("${set test 1.5}${test:greater0}"), "true");
		Assert.assertEquals(DLT.evaluate("${set test -1.5}${test:greater0}"), "false");
	}

	@Test
	public void evaluateModifierNotBlank() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:notBlank}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"\"}${test:notBlank}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \" \"}${test:notBlank}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \" test \"}${test:notBlank}"), "true");
	}

	@Test
	public void evaluateModifierIsUUID() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isUUID}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"123\"}${test:isUUID}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"d3348397-0cda-40ec-a7d2-a444964ab7b1\"}${test:isUUID}"), "true");
	}

	@Test
	public void evaluateModifierIsEmail() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isEmail}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"123\"}${test:isEmail}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"test@s42.com\"}${test:isEmail}"), "true");
	}

	@Test
	public void evaluateModifierIsVariableName() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isVariableName}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"#123\"}${test:isVariableName}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"var1\"}${test:isVariableName}"), "true");
	}

	@Test
	public void evaluateModifierIsVariableNameOrNull() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isVariableNameOrNull}"), "true");
		Assert.assertEquals(DLT.evaluate("${set test \"#123\"}${test:isVariableNameOrNull}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"var1\"}${test:isVariableNameOrNull}"), "true");
	}

	@Test
	public void evaluateModifierIsMethodName() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isMethodName}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"#123\"}${test:isMethodName}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"functionCall\"}${test:isMethodName}"), "true");
	}

	@Test
	public void evaluateModifierIsFloat() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isFloat}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"text\"}${test:isFloat}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test true}${test:isFloat}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1}${test:isFloat}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1L}${test:isFloat}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5}${test:isFloat}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5f}${test:isFloat}"), "true");
	}

	@Test
	public void evaluateModifierIsDouble() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isDouble}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"text\"}${test:isDouble}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test true}${test:isDouble}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1}${test:isDouble}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1L}${test:isDouble}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5}${test:isDouble}"), "true");
		Assert.assertEquals(DLT.evaluate("${set test 1.5f}${test:isDouble}"), "false");
	}

	@Test
	public void evaluateModifierIsInteger() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isInteger}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"text\"}${test:isInteger}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test true}${test:isInteger}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1}${test:isInteger}"), "true");
		Assert.assertEquals(DLT.evaluate("${set test 1L}${test:isInteger}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5}${test:isInteger}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5f}${test:isInteger}"), "false");
	}

	@Test
	public void evaluateModifierIsLong() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isLong}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"text\"}${test:isLong}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test true}${test:isLong}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1}${test:isLong}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1L}${test:isLong}"), "true");
		Assert.assertEquals(DLT.evaluate("${set test 1.5}${test:isLong}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5f}${test:isLong}"), "false");
	}

	@Test
	public void evaluateModifierIsBoolean() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isBoolean}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"text\"}${test:isBoolean}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test true}${test:isBoolean}"), "true");
		Assert.assertEquals(DLT.evaluate("${set test 1}${test:isBoolean}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1L}${test:isBoolean}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5}${test:isBoolean}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5f}${test:isBoolean}"), "false");
	}

	@Test
	public void evaluateModifierIsString() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isString}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test \"text\"}${test:isString}"), "true");
		Assert.assertEquals(DLT.evaluate("${set test true}${test:isString}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1}${test:isString}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1L}${test:isString}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5}${test:isString}"), "false");
		Assert.assertEquals(DLT.evaluate("${set test 1.5f}${test:isString}"), "false");
	}

	@Test
	public void evaluateModifierIsArray() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isArray}"), "false");
		Assert.assertEquals(DLT.evaluate("${emptyArray:isArray}"), "true");
		TemplateContext context = new DefaultTemplateContext();
		context.setBinding("array", new Object[]{"A", "B", "C", "D"});
		Assert.assertEquals(DLT.evaluate("${array:isArray}", context), "true");
	}

	@Test
	public void evaluateModifierIsNotEmptyArray() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:isNotEmptyArray}"), "false");
		Assert.assertEquals(DLT.evaluate("${emptyArray:isNotEmptyArray}"), "false");
		TemplateContext context = new DefaultTemplateContext();
		context.setBinding("list", new Object[]{"A", "B", "C", "D"});
		Assert.assertEquals(DLT.evaluate("${list:isNotEmptyArray}", context), "true");
	}

	@Test
	public void evaluateModifierUpperCase() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:upperCase}"), "");
		Assert.assertEquals(DLT.evaluate("${set test \"aBc\"}${test:upperCase}"), "ABC");
	}

	@Test
	public void evaluateModifierUpperCaseFirst() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:upperCaseFirst}"), "");
		Assert.assertEquals(DLT.evaluate("${set test \"aBc\"}${test:upperCaseFirst}"), "ABc");
	}

	@Test
	public void evaluateModifierLowerCase() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:lowerCase}"), "");
		Assert.assertEquals(DLT.evaluate("${set test \"aBcD\"}${test:lowerCase}"), "abcd");
	}

	@Test
	public void evaluateModifierLowerCaseFirst() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:lowerCaseFirst}"), "");
		Assert.assertEquals(DLT.evaluate("${set test \"ABc\"}${test:lowerCaseFirst}"), "aBc");
	}

	@Test
	public void evaluateModifierIndent() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:indent1}"), "");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent1}"), "first\n\ttext");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent2}"), "first\n\t\ttext");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent3}"), "first\n\t\t\ttext");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent4}"), "first\n\t\t\t\ttext");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent5}"), "first\n\t\t\t\t\ttext");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent6}"), "first\n\t\t\t\t\t\ttext");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent7}"), "first\n\t\t\t\t\t\t\ttext");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent8}"), "first\n\t\t\t\t\t\t\t\ttext");
		Assert.assertEquals(DLT.evaluate("${set test \"first\\ntext\"}${test:indent9}"), "first\n\t\t\t\t\t\t\t\t\ttext");
	}

	@Test
	public void evaluateModifierRemoveWhiteSpaces() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:removeWhiteSpaces}"), "");
		Assert.assertEquals(DLT.evaluate("${set test \" Test Text \"}${test:removeWhiteSpaces}"), "TestText");
	}

	@Test
	public void evaluateModifierEscapeHtml() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:escapeHtml}"), "");
		Assert.assertEquals(DLT.evaluate("${set test \"<div>OK</div>\"}${test:escapeHtml}"), "&lt;div&gt;OK&lt;/div&gt;");
	}

	@Test
	public void evaluateModifierEscapeString() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:escapeString}"), "");
		Assert.assertEquals(DLT.evaluate("${set test \"Test\\nText\"}${test:escapeString}"), "Test\\nText");
	}

	@Test
	public void evaluateModifierTwoDigit() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:twoDigit}"), "");
		Assert.assertEquals(DLT.evaluate("${set test 2}${test:twoDigit}"), "02");
		Assert.assertEquals(DLT.evaluate("${set test 21}${test:twoDigit}"), "21");
		Assert.assertEquals(DLT.evaluate("${set test 213}${test:twoDigit}"), "213");
		Assert.assertEquals(DLT.evaluate("${set test 2L}${test:twoDigit}"), "02");
		Assert.assertEquals(DLT.evaluate("${set test 21L}${test:twoDigit}"), "21");
		Assert.assertEquals(DLT.evaluate("${set test 213L}${test:twoDigit}"), "213");
	}

	@Test
	public void evaluateModifierThreeDigit() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:threeDigit}"), "");
		Assert.assertEquals(DLT.evaluate("${set test 2}${test:threeDigit}"), "002");
		Assert.assertEquals(DLT.evaluate("${set test 21}${test:threeDigit}"), "021");
		Assert.assertEquals(DLT.evaluate("${set test 213}${test:threeDigit}"), "213");
		Assert.assertEquals(DLT.evaluate("${set test 2134}${test:threeDigit}"), "2134");
		Assert.assertEquals(DLT.evaluate("${set test 2L}${test:threeDigit}"), "002");
		Assert.assertEquals(DLT.evaluate("${set test 21L}${test:threeDigit}"), "021");
		Assert.assertEquals(DLT.evaluate("${set test 213L}${test:threeDigit}"), "213");
		Assert.assertEquals(DLT.evaluate("${set test 2134L}${test:threeDigit}"), "2134");
	}

	@Test
	public void evaluateModifierToArray() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:toArray:length}"), "0");
		Assert.assertEquals(DLT.evaluate("${emptyArray:toArray:length}"), "0");
		TemplateContext context = new DefaultTemplateContext();
		List list = new ArrayList();
		list.add("A");
		list.add("B");
		list.add("C");
		context.setBinding("list", list);
		Assert.assertEquals(DLT.evaluate("${list:toArray:length}", context), "3");
		context = new DefaultTemplateContext();
		Set set = new HashSet();
		set.add("A");
		set.add("B");
		set.add("C");
		context.setBinding("setV", set);
		Assert.assertEquals(DLT.evaluate("${setV:toArray:length}", context), "3");
	}

	@Test
	public void evaluateModifierToString() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:toString}"), "");
		Assert.assertEquals(DLT.evaluate("${set test 2}${test:toString}"), "2");
		Assert.assertEquals(DLT.evaluate("${set test 2.5}${test:toString}"), "2.5");
		Assert.assertEquals(DLT.evaluate("${set test \"ABC\"}${test:toString}"), "ABC");
	}

	@Test
	public void evaluateModifierSqrt() throws Exception
	{
		Assert.assertEquals(DLT.evaluate("${test:sqrt}"), "0.0");
		Assert.assertEquals(DLT.evaluate("${set test 9}${test:sqrt}"), "3.0");
		Assert.assertEquals(DLT.evaluate("${set test 9.0}${test:sqrt}"), "3.0");
	}
}
