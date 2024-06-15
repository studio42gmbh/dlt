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

import de.s42.base.compile.InvalidCompilation;
import de.s42.base.files.FilesHelper;
import de.s42.base.resources.ResourceHelper;
import de.s42.dlt.parser.CompiledTemplate;
import de.s42.dlt.parser.DefaultTemplateContext;
import de.s42.dlt.parser.TemplateCallable;
import de.s42.dlt.parser.TemplateCompilerOptions;
import de.s42.dlt.parser.TemplateContext;
import de.s42.dlt.parser.TemplateLoadable;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class DLTTest
{

	private final static Logger log = LogManager.getLogger(DLTTest.class.getName());

	public static class TestTemplateContext extends DefaultTemplateContext
	{

		@Override
		public String resolveTemplate(String templateId) throws Exception
		{
			//log.info("Resoved {}", templateId);

			return ResourceHelper.getResourceAsString(DLTTest.class, templateId).orElseThrow(() -> {
				return new FileNotFoundException("Template with id '" + templateId + "' not found");
			});
		}
	}

	public static class LoadInit implements TemplateLoadable
	{

		@Override
		public Object load(TemplateContext context, Object... parameters)
		{
			//log.info("Loaded");

			//load a user into the context
			DLTTest.User user = new DLTTest.User();
			user.setFirstName(parameters[0].toString());
			user.setInfos(new Object[]{"See", "Think", "Plan", "Act"});
			context.setBinding("user", user);

			// the returned value is set as binding value -> in this case it allows to check for existence in later if or ifnot
			return true;
		}
	}

	public static class SecretCodeGenerator implements TemplateCallable
	{

		@Override
		public Object call(TemplateContext context, Object... parameters)
		{
			//log.info("Called");

			return "" + parameters[0] + "#" + Math.random();
		}
	}

	public static class User
	{

		private String firstName;
		private Object[] infos;

		public String getFirstName()
		{
			return firstName;
		}

		public void setFirstName(String firstName)
		{
			this.firstName = firstName;
		}

		public Object[] getInfos()
		{
			return infos;
		}

		public void setInfos(Object[] infos)
		{
			this.infos = infos;
		}
	}

	protected void printResult(String result)
	{
		/*log.debug(
			"\n"
			+ "------------------------------------------------------------------------\n"
			+ "{}\n"
			+ "------------------------------------------------------------------------",
			result);*/
	}

	@Test(expectedExceptions = {InvalidCompilation.class})
	public void evaluateInvalidClosingFor() throws Exception
	{
		String content = "${/for}";
		DLT.evaluate(content);
	}

	@Test(expectedExceptions = {InvalidCompilation.class})
	public void evaluateInvalidClosingIfNotInIf() throws Exception
	{
		String content = "${if true}${else}${/ifnot}";
		DLT.evaluate(content);
	}

	@Test(expectedExceptions = {InvalidCompilation.class})
	public void evaluateInvalidClosingIf() throws Exception
	{
		String content = "${/if}";
		DLT.evaluate(content);
	}

	@Test(expectedExceptions = {InvalidCompilation.class})
	public void evaluateInvalidElse() throws Exception
	{
		String content = "${else}";
		DLT.evaluate(content);
	}

	@Test(expectedExceptions = {InvalidCompilation.class})
	public void evaluateInvalidSectionClosing() throws Exception
	{
		String content = "${/section}";
		DLT.evaluate(content);
	}

	@Test
	public void evaluateIfElseIfElse() throws Exception
	{
		String content = "${if false}NOT${elseif true}WORKS${else}NOT${/if}";
		String evaluated = DLT.evaluate(content);
		Assert.assertEquals(evaluated, "WORKS");
	}

	@Test
	public void evaluateIfElseIfNotElse() throws Exception
	{
		String content = "${if false}NOT${elseifnot false}WORKS${else}NOT${/if}";
		String evaluated = DLT.evaluate(content);
		Assert.assertEquals(evaluated, "WORKS");
	}

	@Test
	public void evaluateIf() throws Exception
	{
		String content = "START-${if true}INIF${else}NOTSHOWN${/if}-END";
		String evaluated = DLT.evaluate(content);
		Assert.assertEquals(evaluated, "START-INIF-END");
	}

	/**
	 * Make sure for and its specials x#index x#last and x#first work properly
	 *
	 * @throws Exception
	 */
	@Test
	public void evaluateForTestingFirstIndexAndLastContextVars() throws Exception
	{
		String[] data = new String[]{"A", "B", "C"};
		String content = "${for i data}${if i#first}!${/if}${i}${i#index}${ifnot i#last},${/ifnot}${/for}${for i data}${if i#first}!${/if}${i}${i#index}${ifnot i#last},${/ifnot}${/for}";
		DefaultTemplateContext context = new DefaultTemplateContext();
		context.setBinding("data", data);
		String evaluated = DLT.evaluate(content, context);
		Assert.assertEquals(evaluated, "!A0,B1,C2!A0,B1,C2");
	}

	@Test
	public void evaluateIfWithUnknownSymbol() throws Exception
	{
		String content = "${if NOTDEF_NOW}DEF${else}NOTDEF${/if}";
		String evaluated = DLT.evaluate(content);
		Assert.assertEquals(evaluated, "NOTDEF");
	}

	@Test
	public void evaluateIfNotWithUnknownSymbol() throws Exception
	{
		String content = "${ifnot NOTDEF_NOW}NOTDEF${else}DEF${/ifnot}";
		String evaluated = DLT.evaluate(content);
		Assert.assertEquals(evaluated, "NOTDEF");
	}

	@Test
	public void evaluateIfWithComparator() throws Exception
	{
		String content = "START-${if true true}INIF${else}NOTSHOWN${/if}-END";
		String evaluated = DLT.evaluate(content);
		Assert.assertEquals(evaluated, "START-INIF-END");
	}

	@Test
	public void evaluateCode() throws Exception
	{
		String content = "${$ int x = 5 * 5;context.setBinding(\"calculated\", x + 17);}${calculated}";
		String evaluated = DLT.evaluate(content);
		Assert.assertEquals(evaluated, "42");
	}

	@Test
	public void evaluateSection() throws Exception
	{
		String content = "MAIN${section SUB}SUBCONTENT${/section}SEC";
		DefaultTemplateContext context = new DefaultTemplateContext();
		String evaluated = DLT.evaluate(content, context);
		Assert.assertEquals(evaluated, "MAINSEC");
		Assert.assertEquals(context.getContent("SUB"), "SUBCONTENT");
	}

	@Test
	public void evaluateSimpleWithBindingMap() throws Exception
	{
		String template = "Test ${testInt} ${testString}";
		Map<String, Object> bindings = new HashMap<>();
		bindings.put("testInt", 42);
		bindings.put("testString", "Test123");

		String result = DLT.evaluate(template, bindings);

		printResult(result);
	}

	@Test
	public void compileAndEvaluateSimpleWithBindingMap() throws Exception
	{
		String template = "Test ${testInt} ${testString}";
		Map<String, Object> bindings = new HashMap<>();
		bindings.put("testInt", 42);
		bindings.put("testString", "Test123");
		CompiledTemplate compiled = DLT.compile(template);

		String result = compiled.evaluate(bindings);

		printResult(result);
	}

	@Test
	public void evaluateSimpleFromFile() throws Exception
	{
		// eval template
		String result = (new TestTemplateContext()).evaluate("DLTTest.simple.dlt");

		printResult(result);
	}

	@Test
	public void complileAndEvaluateCompiledFromTemplateFile() throws Exception
	{
		TestTemplateContext context = new TestTemplateContext();

		// compile template
		String content = context.resolveTemplate("DLTTest.template.dlt");
		TemplateCompilerOptions options = new TemplateCompilerOptions();
		options.setDebug(true);
		options.setTemplateId("DLTTest.template.dlt");
		options.setClassName("DLTTestTemplate");
		options.setPackageName("de.s42.dlt");
		CompiledTemplate compiled = DLT.compile(content, options);

		//generate code and write to file
		String code = DLT.generate(content, options);
		FilesHelper.writeStringToFile(FilesHelper.getWorkingDirectory().resolve("src/test/java/de/s42/dlt/DLTTestTemplate.java"), code);

		//evaluate compiled template
		context.setBinding("ADCAMPAIGN", true);
		context.setBinding("productName", "DL Template");
		String result = compiled.evaluate(context);

		printResult(result);
		printResult(context.getContent("SENDINFO"));
	}

}
