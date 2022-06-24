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

import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public interface CompiledTemplate
{

	public String evaluate(TemplateContext context) throws Exception;

	public default String evaluate() throws Exception
	{
		return evaluate(new DefaultTemplateContext());
	}

	public default String evaluate(Map<String, Object> bindings) throws Exception
	{
		TemplateContext context = new DefaultTemplateContext();
		context.addBindings(bindings);
		return evaluate(context);
	}
}
