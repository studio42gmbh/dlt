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
 * Returns the two digited string of an integer or long<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${set test 1}
 * ...
 * ${test:twoDigit}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class TwoDigit implements Modifier<Object, String>
{

	@Override
	public String apply(Object value)
	{
		if (value == null) {
			return null;
		}

		String n;

		switch (value) {
			case Integer integer ->
				n = Integer.toString(integer);
			case Long aLong ->
				n = Long.toString(aLong);
			case String string ->
				n = string;
			default -> {
				return null;
			}
		}

		if (n.length() < 2) {
			n = "0" + n;
		}

		return n;
	}
}
