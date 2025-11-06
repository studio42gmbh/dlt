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
package de.s42.dlt.parser.modifiers.math;

import de.s42.base.conversion.ConversionHelper;
import de.s42.dlt.parser.Modifier;

/**
 * Tests if the given value is greater than 0<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${set value 1.5}
 * ...
 * ${if value:greater0} <- will be true
 * ...
 * ${/if}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class Greater0 implements Modifier<Object, Boolean>
{

	@Override
	public Boolean apply(Object value)
	{
		if (value == null) {
			return false;
		}

		return (double) ConversionHelper.convert(value, double.class) > 0.0;
	}
}
