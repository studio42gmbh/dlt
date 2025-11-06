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

import de.s42.dlt.parser.Modifier;

/**
 * Returns the version of a given string where the uppercases are replaced by _lowercase<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${set test "thisIsAName"} - is set to "this_is_a_name"
 * ...
 * ${test:constantCase}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class ConstantCase implements Modifier<String, String>
{

	@Override
	public String apply(String value)
	{
		if (value == null) {
			return null;
		}

		return value.replaceAll("([A-Z])", "_$1").toUpperCase();
	}
}
