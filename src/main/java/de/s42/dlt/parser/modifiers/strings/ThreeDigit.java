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
 * Returns the three digited string of an integer or long<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${set test 1}
 * ...
 * ${test:threeDigit}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class ThreeDigit implements Modifier<Object, String>
{

	@Override
	public String apply(Object value)
	{
		if (value == null) {
			return null;
		}

		String n;

		if (value instanceof Integer) {
			n = Integer.toString((Integer) value);
		} else if (value instanceof Long) {
			n = Long.toString((Long) value);
		} else if (value instanceof String) {
			n = (String) value;
		} else {
			return null;
		}

		if (n.length() < 2) {
			n = "00" + n;
		} else if (n.length() < 3) {
			n = "0" + n;
		}

		return n;
	}
}
