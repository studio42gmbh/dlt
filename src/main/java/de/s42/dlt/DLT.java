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

import de.s42.base.files.FilesHelper;
import de.s42.dlt.parser.CompiledTemplate;
import de.s42.dlt.parser.DefaultTemplateContext;
import de.s42.dlt.parser.TemplateCompiler;
import de.s42.dlt.parser.TemplateCompilerOptions;
import de.s42.dlt.parser.TemplateContext;
import java.nio.file.Path;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public final class DLT
{

	public final static String DEFAULT_FILE_ENDING = ".dlt";

	private DLT()
	{
	}

	public final static void emptyCache()
	{
		TemplateCompiler.emptyCache();
	}

	public final static String evaluate(Path source) throws Exception
	{
		assert source != null : "source != null";

		return evaluate(FilesHelper.getFileAsString(source));
	}

	public final static String evaluate(String source) throws Exception
	{
		assert source != null : "source != null";

		return evaluate(source, new TemplateCompilerOptions(), new DefaultTemplateContext());
	}

	public final static String evaluate(Path source, TemplateContext context) throws Exception
	{
		assert source != null : "source != null";
		assert context != null : "context != null";

		return evaluate(FilesHelper.getFileAsString(source), context);
	}

	public final static String evaluate(String source, Map<String, Object> bindings) throws Exception
	{
		assert source != null : "source != null";
		assert bindings != null : "bindings != null";

		TemplateContext context = new DefaultTemplateContext();
		context.addBindings(bindings);
		return evaluate(source, new TemplateCompilerOptions(), context);
	}

	public final static String evaluate(String source, TemplateContext context) throws Exception
	{
		assert source != null : "source != null";
		assert context != null : "context != null";

		return evaluate(source, new TemplateCompilerOptions(), context);
	}

	public final static String evaluate(String source, TemplateCompilerOptions options, TemplateContext context) throws Exception
	{
		assert source != null : "source != null";
		assert context != null : "context != null";

		return compile(source, options).evaluate(context);
	}

	public final static CompiledTemplate compile(Path source) throws Exception
	{
		assert source != null : "source != null";

		return compile(FilesHelper.getFileAsString(source), new TemplateCompilerOptions());
	}

	public final static CompiledTemplate compile(String source) throws Exception
	{
		assert source != null : "source != null";

		return compile(source, new TemplateCompilerOptions());
	}

	public final static CompiledTemplate compile(String source, String templateId) throws Exception
	{
		assert source != null : "source != null";
		assert templateId != null : "templateId != null";

		return compile(source, new TemplateCompilerOptions(templateId));
	}

	public final static CompiledTemplate compile(Path source, TemplateCompilerOptions options) throws Exception
	{
		assert source != null : "source != null";
		assert options != null : "options != null";

		return compile(FilesHelper.getFileAsString(source), options);
	}

	public final static CompiledTemplate compile(String source, TemplateCompilerOptions options) throws Exception
	{
		assert source != null : "source != null";
		assert options != null : "options != null";

		return TemplateCompiler.compile(source, options);
	}

	public final static String generate(String source) throws Exception
	{
		assert source != null : "source != null";

		return generate(source, new TemplateCompilerOptions());
	}

	public final static String generate(String source, TemplateCompilerOptions options) throws Exception
	{
		assert source != null : "source != null";
		assert options != null : "options != null";

		return TemplateCompiler.generate(source, options);
	}

}
