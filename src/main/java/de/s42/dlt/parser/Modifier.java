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
package de.s42.dlt.parser;

import de.s42.base.strings.StringHelper;
import java.util.function.Function;

/**
 * This class allows to add a new Modifier
 *
 * @author Benjamin Schiller
 * @param <T> Input type
 * @param <S> Resulting type
 */
public interface Modifier<T extends Object, S extends Object> extends Function<T, S>
{

	/**
	 * Defines the mapping used for this modifier - it defaults to using the class name with a first lowercase letter So
	 * the class Exists - will map as 'exists'
	 *
	 * @return Camelcase first letter lower of the modifier class
	 */
	public default String getKeyword()
	{
		return StringHelper.lowerCaseFirst(getClass().getSimpleName());
	}
}
