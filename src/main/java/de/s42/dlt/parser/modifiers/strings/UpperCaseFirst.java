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
 * Returns the first letter uppercase version of a given string<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${set test "tExT"}
 * ...
 * ${test:upperCase}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class UpperCaseFirst implements Modifier<String, String>
{

	@Override
	public String apply(String value)
	{
		if (value == null) {
			return null;
		}

		return StringHelper.upperCaseFirst(value);
	}
}
