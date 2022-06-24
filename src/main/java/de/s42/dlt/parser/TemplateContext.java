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

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 */
public interface TemplateContext
{

	public void reset();

	// CONTENT AND SECTIONS
	public void pushSection(String section);

	public void popSection();

	public void append(Object text);

	public void append(String section, Object text);

	public String getContent();

	public String getContent(String section);

	public void setContent(String content);

	public void setContent(String section, String content);

	// BINDINGS
	public void setBinding(String key, Object value);

	public void setBinding(String[] pathElements, Object[] value) throws Exception;

	public void setBinding(String[] pathElements, Object value) throws Exception;

	public void addBindings(Map<String, Object> bindings);

	public void incrementBinding(String key);

	public void decrementBinding(String key);

	public void unsetBinding(String key);

	public boolean hasBinding(String key);

	public Object getBinding(String key);

	public Object resolveBinding(String key);

	public Object resolveBinding(String[] pathElements, String[] modifiers) throws Exception;

	public Set<String> getBindingKeys();

	// MODIFIERS
	public void addModifier(Modifier modifier);

	public Modifier getModifier(String key) throws Exception;

	public Object applyModifier(Object value, String key) throws Exception;

	public void removeModifier(String key);

	public Set<String> getModifierKeys();

	// SUB TEMPLATES
	public String evaluate(String templateId) throws Exception;

	// TEMPLATE LOADABLE AND CALLABLE
	public void load(String key, String load, Object... parameters) throws Exception;

	public Object call(String key, Object... parameters) throws Exception;
}
