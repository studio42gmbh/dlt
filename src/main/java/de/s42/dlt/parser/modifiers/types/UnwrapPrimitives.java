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
package de.s42.dlt.parser.modifiers.types;

import de.s42.base.conversion.ConversionHelper;
import de.s42.dlt.parser.Modifier;

/**
 * unwraps a given class to its primitve if it is a boxed primitve type. otherwise returns the class itself<br>
 * Example:<br>
 * <pre>
 * {@code
 * // let value be a Class
 * ...
 * ${set classValue value:unwrapPrimitives}
 * ...
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class UnwrapPrimitives implements Modifier<Class, Class>
{

	@Override
	public Class apply(Class value)
	{
		return ConversionHelper.unwrapPrimitives(value);
	}
}
