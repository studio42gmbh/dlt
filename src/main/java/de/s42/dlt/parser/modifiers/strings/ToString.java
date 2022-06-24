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
 * converts given value to string with .toString<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${value:toString}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class ToString implements Modifier<Object, String>
{

	@Override
	public String apply(Object value)
	{
		if (value == null) {
			return "";
		}

		return value.toString();
	}
}
