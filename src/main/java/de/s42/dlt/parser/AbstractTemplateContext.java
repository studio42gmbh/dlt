/*
 * Copyright Studio 42 GmbH 2020. All rights reserved.
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

import de.s42.base.beans.BeanHelper;
import de.s42.base.beans.BeanInfo;
import de.s42.base.conversion.ConversionHelper;
import de.s42.base.files.FilesHelper;
import de.s42.dlt.DLT;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ATTENTION: For performance reasons and usage aspects TemplateContext is NOT threadsafe!
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractTemplateContext implements TemplateContext
{

	private final static Logger log = LogManager.getLogger(AbstractTemplateContext.class.getName());

	private final Map<String, Object> bindings = new HashMap<>();
	private final Map<String, Modifier> modifiers = new HashMap<>();
	private StringBuilder content = new StringBuilder();
	private final Map<String, StringBuilder> sectionContents = new HashMap<>();
	private final Stack<String> sectionStack = new Stack();
	private ClassLoader classLoader = AbstractTemplateContext.class.getClassLoader();
	private String classPath;
	private Path workingDirectory;
	private boolean cacheCompiledTemplates;

	@Override
	public void pushSection(String section)
	{
		sectionContents.putIfAbsent(section, new StringBuilder());
		sectionStack.push(section);
	}

	@Override
	public void popSection()
	{
		sectionStack.pop();
	}

	@Override
	public String getContent()
	{
		return content.toString();
	}

	@Override
	public String getContent(String section)
	{
		return sectionContents.get(section).toString();
	}

	@Override
	public void setContent(String content)
	{
		this.content = new StringBuilder(content);
	}

	@Override
	public void setContent(String section, String content)
	{
		sectionContents.put(section, new StringBuilder(content));
	}

	@Override
	public void append(Object text)
	{
		if (!sectionStack.isEmpty()) {
			sectionContents.get(sectionStack.peek()).append(text);
		} else {
			content.append(text);
		}
	}

	@Override
	public void append(String section, Object text)
	{
		sectionContents.get(section).append(text);
	}

	@Override
	public void reset()
	{
		content = new StringBuilder();
		bindings.clear();
		sectionContents.clear();
		sectionStack.clear();
	}

	@Override
	public Set<String> getBindingKeys()
	{
		return Collections.unmodifiableSet(bindings.keySet());
	}

	@Override
	public Set<String> getModifierKeys()
	{
		return Collections.unmodifiableSet(modifiers.keySet());
	}

	@Override
	public void setBinding(String key, Object value)
	{
		bindings.put(key, value);
	}

	@Override
	public void addBindings(Map<String, Object> bindings)
	{
		this.bindings.putAll(bindings);
	}

	@Override
	public void incrementBinding(String key)
	{
		if (!bindings.containsKey(key)) {
			bindings.put(key, 1);
		} else {
			Object v = bindings.get(key);

			if (v instanceof Integer) {
				v = (Integer) v + 1;
			} else if (v instanceof Long) {
				v = (Long) v + 1L;
			} else if (v instanceof Float) {
				v = (Float) v + 1.0f;
			} else if (v instanceof Double) {
				v = (Double) v + 1.0;
			}

			bindings.put(key, v);
		}
	}

	@Override
	public void decrementBinding(String key)
	{
		if (!bindings.containsKey(key)) {
			bindings.put(key, -1);
		} else {
			Object v = bindings.get(key);

			if (v instanceof Integer) {
				v = (Integer) v - 1;
			} else if (v instanceof Long) {
				v = (Long) v - 1L;
			} else if (v instanceof Float) {
				v = (Float) v - 1.0f;
			} else if (v instanceof Double) {
				v = (Double) v - 1.0;
			}

			bindings.put(key, v);
		}
	}

	@Override
	public void unsetBinding(String key)
	{
		bindings.remove(key);
	}

	@Override
	public boolean hasBinding(String key)
	{
		return bindings.containsKey(key);
	}

	@Override
	public Object getBinding(String key)
	{
		return bindings.get(key);
	}

	@Override
	public Object resolveBinding(String key)
	{
		Object value = getBinding(key);

		//special handling for TemplateCallable
		if (value instanceof TemplateCallable) {
			return ((TemplateCallable) value).call(this);
		}

		//null gets converted to an empty string to not producs 'null' in template generated string
		if (value == null) {
			return "";
		}

		return value;
	}

	@Override
	public void addModifier(Modifier modifier)
	{
		modifiers.put(modifier.getKeyword(), modifier);
	}

	public void addModifiers(Map<String, Modifier> modifiers)
	{
		this.modifiers.putAll(modifiers);
	}

	@Override
	public Modifier getModifier(String key) throws Exception
	{
		Modifier modifier = modifiers.get(key);

		if (modifier == null) {
			throw new Exception("No modifier not found with name " + key);
		}

		return modifier;
	}

	@Override
	public Object applyModifier(Object value, String modifier) throws Exception
	{
		return getModifier(modifier).apply(value);
	}

	@Override
	public void removeModifier(String key)
	{
		modifiers.remove(key);
	}

	@Override
	public Object resolveBinding(String[] pathElements, String[] modifiers) throws Exception
	{
		try {
			Object value = getBinding(pathElements[0]);

			//special handling for TemplateCallable
			if (value instanceof TemplateCallable) {
				return ((TemplateCallable) value).call(this);
			}

			for (int i = 1; i < pathElements.length; ++i) {
				value = resolveProperty(value, pathElements[i]);
			}

			for (int i = 0; i < modifiers.length; ++i) {
				value = applyModifier(value, modifiers[i]);
			}

			if (value instanceof TemplateCallable) {
				return ((TemplateCallable) value).call(this);
			}

			//null gets converted to an empty string to not producs 'null' in template generated string
			if (value == null) {
				return "";
			}

			return value;
		} catch (Exception ex) {
			throw new Exception("Error resolving " + Arrays.toString(pathElements) + " " + Arrays.toString(modifiers) + " - " + ex, ex);
		}
	}

	@Override
	public void setBinding(String[] pathElements, Object[] values) throws Exception
	{
		assert pathElements != null;
		assert values != null;

		// No values -> call with null
		if (values.length == 0) {
			setBinding(pathElements, null);
		} // Just pass through 1 values as is
		else if (values.length == 1) {
			setBinding(pathElements, values[0]);
		} // Concatenate other amounts as string
		else {

			StringBuilder value = new StringBuilder();
			for (Object v : values) {
				value.append(ConversionHelper.convert(v, String.class));
			}

			//log.debug("setComplexBinding", Arrays.toString(pathElements), value);
			setBinding(pathElements, value.toString());
		}
	}

	@Override
	public void setBinding(String[] pathElements, Object value) throws Exception
	{
		assert pathElements != null;
		assert pathElements.length > 0;
		assert value != null;

		//simple 1 length paths behave like setBinding
		if (pathElements.length == 1) {
			setBinding(pathElements[0], value);
			return;
		}

		try {
			Object ref = resolveBinding(pathElements[0]);

			for (int i = 1; i < pathElements.length - 1; ++i) {
				ref = resolveProperty(ref, pathElements[i]);
			}

			BeanInfo info = BeanHelper.getBeanInfo(ref.getClass());

			info.writeConverted(ref, pathElements[pathElements.length - 1], value);

		} catch (Exception ex) {
			throw new Exception("Error resolving " + Arrays.toString(pathElements) + " - " + ex, ex);
		}
	}

	private Object resolveProperty(Object obj, String property) throws Exception
	{
		if (obj == null) {
			throw new Exception("Error resolveProperty - " + property);
		}

		if (obj instanceof JSONObject) {

			return ((JSONObject) obj).opt(property);
		} else if (obj instanceof JSONArray) {

			return ((JSONArray) obj).opt(Integer.parseInt(property));
		} else {

			BeanInfo info = BeanHelper.getBeanInfo(obj.getClass());

			Object prop = info.read(obj, property);

			if (prop instanceof TemplateCallable) {
				return ((TemplateCallable) prop).call(this);
			}

			return prop;
		}
	}

	/**
	 * Override this method to change loading of templates in ${include &lt;templateID&gt;} statements It gets the given
	 * string and shall return the content string of the included template
	 *
	 * @param templateId given id from include statement
	 * @return a string containing the raw template content
	 * @throws Exception if the template can not be resolved
	 */
	public String resolveTemplate(String templateId) throws Exception
	{
		assert getWorkingDirectory().isPresent();

		try {

			Path filePath = getWorkingDirectory().orElseThrow().resolve(templateId);

			if (!FilesHelper.fileExists(filePath)) {
				throw new RuntimeException("File " + filePath + " does not exist");
			}

			return FilesHelper.getFileAsString(filePath);
		} catch (IOException | RuntimeException ex) {
			throw new Exception("Error resolving template '" + templateId + "' - " + ex.getMessage(), ex);
		}
	}

	@Override
	public String evaluate(String templateId) throws Exception
	{
		try {

			String source = resolveTemplate(templateId);

			TemplateCompilerOptions options = new TemplateCompilerOptions();
			options.setClassLoader(getClassLoader());
			options.setClassPath(getClassPath());
			options.setTemplateId(templateId);
			options.setCacheCompiledTemplate(isCacheCompiledTemplates());

			return DLT.evaluate(source, options, this);
		} catch (Exception ex) {
			throw new Exception("Error evaluating template - " + ex.getMessage(), ex);
		}
	}

	@Override
	public void load(String key, String load, Object... parameters) throws Exception
	{
		try {
			Object loaded = Class.forName(load).getConstructor().newInstance();

			if (loaded instanceof TemplateLoadable) {
				loaded = ((TemplateLoadable) loaded).load(this, parameters);
			}

			setBinding(key, loaded);

		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new Exception("Error loading " + key + " " + load + " - " + ex.getMessage(), ex);
		}
	}

	@Override
	public Object call(String key, Object... parameters) throws Exception
	{
		try {
			TemplateCallable callable = (TemplateCallable) getBinding(key);

			if (callable == null) {
				throw new Exception("Binding " + key + " not mapped");
			}

			return callable.call(this, parameters);
		} catch (Exception ex) {
			throw new Exception("Error calling " + key + " - " + ex.getMessage(), ex);
		}
	}

	public Optional<Path> getWorkingDirectory()
	{
		return Optional.ofNullable(workingDirectory);
	}

	public void setWorkingDirectory(Path workingDirectory)
	{
		this.workingDirectory = workingDirectory;
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

	public boolean isCacheCompiledTemplates()
	{
		return cacheCompiledTemplates;
	}

	public void setCacheCompiledTemplates(boolean cacheCompiledTemplates)
	{
		this.cacheCompiledTemplates = cacheCompiledTemplates;
	}
}
