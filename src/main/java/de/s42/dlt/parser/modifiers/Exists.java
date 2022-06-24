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
package de.s42.dlt.parser.modifiers;

import de.s42.dlt.parser.Modifier;

/**
 * tests if the given value exists (!= null)<br>
 * Example:<br>
 * <pre>
 * {@code
 * ...
 * ${if value:exists}
 * ...
 * ${/if}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class Exists implements Modifier<Object, Boolean>
{

	@Override
	public Boolean apply(Object value)
	{
		return (value != null);
	}
}
