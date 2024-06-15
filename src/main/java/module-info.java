/*
 * Copyright Studio 42 GmbH 2020. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For details to the License read https://www.s42m.de/license
 */

module de.sft.dlt
{
	requires org.antlr.antlr4.runtime;
	requires de.sft.log;
	requires de.sft.base;
	requires java.desktop;
	requires java.compiler;
	requires org.json;
	requires de.sft.dl;

	exports de.s42.dlt;
	exports de.s42.dlt.parser;
	exports de.s42.dlt.parser.modifiers;
	exports de.s42.dlt.parser.modifiers.arrays;
	exports de.s42.dlt.parser.modifiers.math;
	exports de.s42.dlt.parser.modifiers.strings;
	exports de.s42.dlt.parser.modifiers.types;

	opens de.s42.dlt;
	opens de.s42.dlt.parser;
	opens de.s42.dlt.parser.modifiers;
	opens de.s42.dlt.parser.modifiers.arrays;
	opens de.s42.dlt.parser.modifiers.math;
	opens de.s42.dlt.parser.modifiers.strings;
	opens de.s42.dlt.parser.modifiers.types;
}
