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
package de.s42.dlt.parser.modifiers.arrays;

import de.s42.base.conversion.ConversionHelper;
import de.s42.dlt.parser.Modifier;
import java.util.Collection;

/**
 * Converts the given input (Collections) to an array<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${set value someList}
 * ...
 * ${for val value:toArray} - turns the collection into an array which gets iterated
 * ...
 * ${/for}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class ToArray implements Modifier<Object, Object[]>
{

	@Override
	public Object[] apply(Object value)
	{
		if (value == null) {
			return new Object[]{};
		}

		if (value instanceof Collection collection) {
			return collection.toArray();
		}

		return ConversionHelper.convert(value, Object[].class);
	}
}
