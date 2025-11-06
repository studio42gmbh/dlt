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
 * Takes the sqrt of the given input<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${set value 4}
 * ...
 * ${if value:sqrt} - is 2
 * ...
 * ${/if}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class Sqrt implements Modifier<Object, Double>
{

	@Override
	public Double apply(Object value)
	{
		if (value == null) {
			return 0.0;
		}

		return (double) Math.sqrt(ConversionHelper.convert(value, double.class));
	}
}
