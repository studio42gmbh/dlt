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

import de.s42.base.date.Now;
import de.s42.base.files.FilesHelper;
import de.s42.dlt.parser.modifiers.Exists;
import de.s42.dlt.parser.modifiers.math.Greater0;
import de.s42.dlt.parser.modifiers.math.Sqrt;
import de.s42.dlt.parser.modifiers.arrays.IsArray;
import de.s42.dlt.parser.modifiers.arrays.IsNotEmptyArray;
import de.s42.dlt.parser.modifiers.types.IsEmail;
import de.s42.dlt.parser.modifiers.types.IsFloat;
import de.s42.dlt.parser.modifiers.strings.IsMethodName;
import de.s42.dlt.parser.modifiers.types.IsUUID;
import de.s42.dlt.parser.modifiers.strings.IsVariableName;
import de.s42.dlt.parser.modifiers.strings.IsVariableNameOrNull;
import de.s42.dlt.parser.modifiers.arrays.Length;
import de.s42.dlt.parser.modifiers.arrays.ToArray;
import de.s42.dlt.parser.modifiers.strings.ConstantCase;
import de.s42.dlt.parser.modifiers.strings.EscapeHtml;
import de.s42.dlt.parser.modifiers.strings.EscapeString;
import de.s42.dlt.parser.modifiers.strings.Indent;
import de.s42.dlt.parser.modifiers.strings.LowerCase;
import de.s42.dlt.parser.modifiers.strings.LowerCaseFirst;
import de.s42.dlt.parser.modifiers.types.IsBoolean;
import de.s42.dlt.parser.modifiers.types.IsDouble;
import de.s42.dlt.parser.modifiers.types.IsInteger;
import de.s42.dlt.parser.modifiers.types.IsLong;
import de.s42.dlt.parser.modifiers.types.IsString;
import de.s42.dlt.parser.modifiers.strings.NotBlank;
import de.s42.dlt.parser.modifiers.strings.RemoveWhiteSpaces;
import de.s42.dlt.parser.modifiers.strings.ThreeDigit;
import de.s42.dlt.parser.modifiers.strings.ToString;
import de.s42.dlt.parser.modifiers.strings.TwoDigit;
import de.s42.dlt.parser.modifiers.strings.UpperCase;
import de.s42.dlt.parser.modifiers.strings.UpperCaseFirst;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Benjamin Schiller
 */
public class DefaultTemplateContext extends AbstractTemplateContext
{

	private final static Map<String, Modifier> defaultModifiers = new HashMap<>();
	private final static Map<String, Object> defaultBindings = new HashMap<>();

	public static synchronized void prepareBinding(String key, Object value)
	{
		defaultBindings.put(key, value);
	}

	public static synchronized void prepareModifier(Modifier modifier)
	{
		defaultModifiers.put(modifier.getKeyword(), modifier);
	}

	static {

		prepareBinding("true", Boolean.TRUE);
		prepareBinding("false", Boolean.FALSE);
		prepareBinding("void", "void");
		prepareBinding("emptyArray", new Object[]{});

		prepareModifier(new Exists());
		prepareModifier(new Length());
		prepareModifier(new Greater0());
		prepareModifier(new Exists());
		prepareModifier(new NotBlank());
		prepareModifier(new IsUUID());
		prepareModifier(new IsEmail());
		prepareModifier(new IsVariableName());
		prepareModifier(new IsVariableNameOrNull());
		prepareModifier(new IsMethodName());
		prepareModifier(new IsFloat());
		prepareModifier(new IsDouble());
		prepareModifier(new IsInteger());
		prepareModifier(new IsLong());
		prepareModifier(new IsBoolean());
		prepareModifier(new IsString());
		prepareModifier(new IsArray());
		prepareModifier(new IsNotEmptyArray());
		prepareModifier(new UpperCase());
		prepareModifier(new UpperCaseFirst());
		prepareModifier(new LowerCase());
		prepareModifier(new LowerCaseFirst());
		prepareModifier(new Indent(1));
		prepareModifier(new Indent(2));
		prepareModifier(new Indent(3));
		prepareModifier(new Indent(4));
		prepareModifier(new Indent(5));
		prepareModifier(new Indent(6));
		prepareModifier(new Indent(7));
		prepareModifier(new Indent(8));
		prepareModifier(new Indent(9));
		prepareModifier(new RemoveWhiteSpaces());
		prepareModifier(new ConstantCase());
		prepareModifier(new EscapeHtml());
		prepareModifier(new EscapeString());
		prepareModifier(new TwoDigit());
		prepareModifier(new ThreeDigit());
		prepareModifier(new ToArray());
		prepareModifier(new ToString());
		prepareModifier(new Sqrt());
	}

	public DefaultTemplateContext()
	{
		initBaseValues();
		initDefaultBindings();
		initDefaultModifiers();
	}

	private void initBaseValues()
	{
		setWorkingDirectory(FilesHelper.getWorkingDirectory());
	}

	private void initDefaultBindings()
	{
		addBindings(defaultBindings);
		setBinding("now", new Now()); //has to be added newly as it shall represent the time of init not vm start
	}

	private void initDefaultModifiers()
	{
		addModifiers(defaultModifiers);
	}

	@Override
	public void reset()
	{
		super.reset();
		initDefaultBindings();
	}
}
