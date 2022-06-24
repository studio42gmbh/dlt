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
package de.s42.dlt.parser.modifiers.strings;

import de.s42.base.strings.StringHelper;
import de.s42.dlt.parser.Modifier;

/**
 * Returns an indented version of a given multiline string - is used for nice template generation<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${set test "text"}
 * ...
 * ${test:indent1}
 * ${test:indent2}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class Indent implements Modifier<String, String>
{

	private final int depth;
	private final String replacement;

	public Indent(int depth)
	{
		this.depth = depth;
		StringBuilder str = new StringBuilder("\n");
		for (int i = 0; i < depth; ++i) {
			str.append("\t");
		}
		replacement = str.toString();
	}

	@Override
	public String apply(String value)
	{
		if (value == null) {
			return null;
		}

		return value.replace("\n", replacement);
	}

	@Override
	public String getKeyword()
	{
		return StringHelper.lowerCaseFirst(getClass().getSimpleName()) + depth;
	}
}
