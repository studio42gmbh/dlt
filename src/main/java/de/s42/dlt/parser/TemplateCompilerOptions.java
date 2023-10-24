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
package de.s42.dlt.parser;

/**
 *
 * @author Benjamin Schiller
 */
public class TemplateCompilerOptions
{

	private final static String DEFAULT_GENERATED_CLASS_NAME = "Compiled";
	private final static String DEFAULT_GENERATED_PACKAGE_NAME = "de.s42.dlt.template";

	private String className = DEFAULT_GENERATED_CLASS_NAME;
	private String packageName = DEFAULT_GENERATED_PACKAGE_NAME;
	private ClassLoader classLoader = getClass().getClassLoader();
	private String classPath;
	private String templateId = "Unnamed";
	private boolean debug = false;
	private boolean cacheCompiledTemplate = true;

	public TemplateCompilerOptions()
	{
	}

	public TemplateCompilerOptions(String templateId)
	{
		this.templateId = templateId;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public ClassLoader getClassLoader()
	{
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader)
	{
		this.classLoader = classLoader;
	}

	public String getClassPath()
	{
		return classPath;
	}

	public void setClassPath(String classPath)
	{
		this.classPath = classPath;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public String getTemplateId()
	{
		return templateId;
	}

	public void setTemplateId(String templateId)
	{
		this.templateId = templateId;
	}

	public boolean isCacheCompiledTemplate()
	{
		return cacheCompiledTemplate;
	}

	public void setCacheCompiledTemplate(boolean cacheCompiledTemplate)
	{
		this.cacheCompiledTemplate = cacheCompiledTemplate;
	}
}
