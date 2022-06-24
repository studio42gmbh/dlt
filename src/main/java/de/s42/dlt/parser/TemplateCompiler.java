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

import de.s42.base.compile.CompileHelper;
import de.s42.base.compile.InvalidCompilation;
import de.s42.dlt.parser.antlr.DLTLexer;
import de.s42.dlt.parser.antlr.DLTParser;
import de.s42.dlt.parser.antlr.DLTParserBaseListener;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import de.s42.log.LogManager;
import de.s42.log.Logger;

/**
 *
 * @author Benjamin Schiller
 */
public final class TemplateCompiler
{

	private final static Logger log = LogManager.getLogger(TemplateCompiler.class.getName());

	//@todo DLT how to allow purging cache - this singleton map is rather dangerous if you parse many templates or can not ensure unique templateIds
	private static final Map<String, CompiledTemplate> compiledCache = new HashMap();

	private static enum Structure
	{
		IF, IFNOT, FOR, SECTION
	}

	private TemplateCompiler()
	{
	}

	private static class ParserListener extends DLTParserBaseListener
	{

		private final StringBuilder compiled = new StringBuilder();
		private int varCounter = 0;
		private final Stack<Structure> structures = new Stack();
		private final TemplateCompilerOptions options;

		ParserListener(TemplateCompilerOptions options)
		{
			this.options = options;
		}

		protected String createErrorMessage(String reason, ParserRuleContext context) throws RuntimeException
		{
			StringBuilder message = new StringBuilder();
			message
				.append(reason)
				.append("\n\tat ")
				.append(options.getTemplateId())
				.append("(")
				.append(options.getTemplateId())
				.append(":")
				.append(context.start.getLine())
				.append(":")
				.append(context.start.getCharPositionInLine())
				.append(")");

			return message.toString();
		}

		protected String createErrorMessage(String reason, Throwable cause, ParserRuleContext context) throws RuntimeException
		{
			StringBuilder message = new StringBuilder();
			message
				.append(reason)
				.append(" - ")
				.append(cause.getMessage().replace("\"", "")) //@todo DL why is the exception throwing if i leave " in there?
				.append("\n\tat ")
				.append(options.getTemplateId())
				.append("(")
				.append(options.getTemplateId())
				.append(":")
				.append(context.start.getLine())
				.append(":")
				.append(context.start.getCharPositionInLine())
				.append(")");

			return message.toString();
		}

		protected String getCompiled()
		{
			return compiled.toString();
		}

		protected String getNextVarName()
		{
			varCounter++;
			return "var" + varCounter;
		}

		protected void appendDebugInfos(String keyword, ParserRuleContext context)
		{
			if (options.isDebug()) {
				compiled.append("//").append(keyword).append(" ").append(context.start.getLine()).append(":").append(context.start.getCharPositionInLine()).append("\n");
			}
		}

		@Override
		@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
		public void enterWarpTemplate(DLTParser.WarpTemplateContext ctx)
		{
			compiled.append(
				"package " + options.getPackageName() + ";\n"
				+ "\n"
				+ "import de.s42.dlt.parser.CompiledTemplate;\n"
				+ "import de.s42.dlt.parser.TemplateContext;\n"
				+ "import de.s42.base.validation.ValidationHelper;\n"
				+ "\n"
				+ "/*\n"
				+ " * PLEASE DO NOT MODIFY THIS CLASS - IT IS GENERATED BY THE DATA LANGUAGE TEMPLATES COMPILER\n"
				+ " */\n"
				+ "public class " + options.getClassName() + " implements CompiledTemplate\n"
				+ "{\n"
				+ "@Override\n"
				+ "public String evaluate(TemplateContext context) throws Exception\n"
				+ "{\n"
			);
		}

		@Override
		public void exitWarpTemplate(DLTParser.WarpTemplateContext ctx)
		{
			compiled.append("return context.getContent();\n}\n}");
		}

		@Override
		public void enterExpressionRead(DLTParser.ExpressionReadContext ctx)
		{
			appendDebugInfos("read", ctx);

			//no parameters -> simple read
			if (ctx.parameter().isEmpty()) {

				compiled.append("context.append(").append(generateContextQuery(ctx.keyword())).append(");\n");
			} //complex read -> is a call internally -> can just be done on callables
			else {
				compiled.append("context.append(context.call(\"").append(ctx.KEYWORD().getText()).append("\", new Object[] {");

				boolean first = true;
				for (DLTParser.ParameterContext param : ctx.parameter()) {
					if (!first) {
						compiled.append(", ");
					}
					if (param.MACRO_STRING() != null) {
						compiled.append(param.MACRO_STRING());
					} else if (param.MACRO_NUMBER() != null) {
						compiled.append(param.MACRO_NUMBER());
					} else if (param.keyword() != null) {
						compiled.append(generateContextQuery(param.keyword()));
					}
					first = false;
				}

				compiled.append("}));\n");
			}
		}

		@Override
		public void enterText(DLTParser.TextContext ctx)
		{
			appendDebugInfos("text", ctx);

			compiled.append("context.append(\"").append(escapeTextForJavaString(ctx.getText())).append("\");\n");
		}

		@Override
		public void enterExpressionSet(DLTParser.ExpressionSetContext ctx)
		{
			appendDebugInfos("set", ctx);

			//simple 1 length path and 1 length assignment
			if (ctx.path().KEYWORD().size() == 1 && ctx.assignment().size() == 1) {

				compiled.append("context.setBinding(\"").append(ctx.path().KEYWORD(0).getText()).append("\",");

				if (ctx.assignment().get(0).MACRO_STRING() != null) {
					compiled.append(ctx.assignment().get(0).MACRO_STRING()).append(");\n");
				} else if (ctx.assignment().get(0).MACRO_NUMBER() != null) {
					compiled.append(ctx.assignment().get(0).MACRO_NUMBER()).append(");\n");
				} else if (ctx.assignment().get(0).keyword() != null) {
					compiled.append(generateContextQuery(ctx.assignment().get(0).keyword())).append(");\n");
				}
			} else {

				compiled.append("context.setBinding( new String[] { ");

				boolean first = true;
				for (TerminalNode keyword : ctx.path().KEYWORD()) {
					if (!first) {
						compiled.append(", ");
					}
					compiled.append("\"").append(keyword.getText()).append("\"");
					first = false;
				}

				compiled.append("}, new Object[] { ");

				first = true;
				for (DLTParser.AssignmentContext ass : ctx.assignment()) {
					if (!first) {
						compiled.append(", ");
					}
					if (ass.MACRO_STRING() != null) {
						compiled.append(ass.MACRO_STRING());
					} else if (ass.MACRO_NUMBER() != null) {
						compiled.append(ass.MACRO_NUMBER());
					} else if (ass.keyword() != null) {
						compiled.append(generateContextQuery(ass.keyword()));
					}
					first = false;
				}

				compiled.append("});\n");

			}
		}

		@Override
		public void enterExpressionInclude(DLTParser.ExpressionIncludeContext ctx)
		{
			appendDebugInfos("include", ctx);

			if (ctx.inclusion().MACRO_STRING() != null) {
				compiled.append("context.evaluate(").append(ctx.inclusion().MACRO_STRING().getText()).append(");\n");
			} else if (ctx.inclusion().keyword() != null) {
				compiled.append("context.evaluate((String)").append(generateContextQuery(ctx.inclusion().keyword())).append(");\n");
			}
		}

		@Override
		public void enterExpressionUnset(DLTParser.ExpressionUnsetContext ctx)
		{
			appendDebugInfos("unset", ctx);

			compiled.append("context.unsetBinding(\"").append(ctx.KEYWORD().getText()).append("\");\n");
		}

		@Override
		public void enterExpressionInc(DLTParser.ExpressionIncContext ctx)
		{
			appendDebugInfos("inc", ctx);

			compiled.append("context.incrementBinding(\"").append(ctx.KEYWORD().getText()).append("\");\n");
		}

		@Override
		public void enterExpressionDec(DLTParser.ExpressionDecContext ctx)
		{
			appendDebugInfos("dec", ctx);

			compiled.append("context.decrementBinding(\"").append(ctx.KEYWORD().getText()).append("\");\n");
		}

		@Override
		public void enterExpressionIf(DLTParser.ExpressionIfContext ctx)
		{
			appendDebugInfos("if", ctx);

			//simple if -> expects a boolean
			if (ctx.comparator() == null) {
				compiled.append("if (Boolean.TRUE.equals(").append(generateContextQuery(ctx.keyword())).append(")) {\n");
			} //if with a comparator -> 2 
			else {
				if (ctx.comparator().keyword() != null) {
					compiled.append("if (ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(generateContextQuery(ctx.comparator().keyword())).append(")) {\n");
				} else if (ctx.comparator().MACRO_STRING() != null) {
					compiled.append("if (ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(ctx.comparator().MACRO_STRING()).append(")) {\n");
				} else if (ctx.comparator().MACRO_NUMBER() != null) {
					compiled.append("if (ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(ctx.comparator().MACRO_NUMBER()).append(")) {\n");
				}
			}

			structures.push(Structure.IF);
		}

		@Override
		public void enterExpressionEndIf(DLTParser.ExpressionEndIfContext ctx) throws RuntimeException
		{
			try {
				if (structures.pop() != Structure.IF) {
					throw new RuntimeException("Error missing start of IF " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
				}
			} catch (EmptyStackException ex) {
				throw new RuntimeException("Error missing start of IF " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine(), ex);
			}

			appendDebugInfos("end if", ctx);

			compiled.append("}\n");
		}

		@Override
		public void enterExpressionIfnot(DLTParser.ExpressionIfnotContext ctx)
		{
			appendDebugInfos("ifnot", ctx);

			//simple if -> expects a boolean
			if (ctx.comparator() == null) {
				compiled.append("if (!Boolean.TRUE.equals(").append(generateContextQuery(ctx.keyword())).append(")) {\n");
			} //if with a comparator -> 2 
			else {
				if (ctx.comparator().keyword() != null) {
					compiled.append("if (!ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(generateContextQuery(ctx.comparator().keyword())).append(")) {\n");
				} else if (ctx.comparator().MACRO_STRING() != null) {
					compiled.append("if (!ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(ctx.comparator().MACRO_STRING()).append(")) {\n");
				} else if (ctx.comparator().MACRO_NUMBER() != null) {
					compiled.append("if (!ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(ctx.comparator().MACRO_NUMBER()).append(")) {\n");
				}
			}

			structures.push(Structure.IFNOT);
		}

		@Override
		public void enterExpressionElseIf(DLTParser.ExpressionElseIfContext ctx)
		{
			try {
				if (structures.peek() != Structure.IF && structures.peek() != Structure.IFNOT) {
					throw new RuntimeException("Error missing start of IF or IFNOT " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
				}
			} catch (EmptyStackException ex) {
				throw new RuntimeException("Error missing start of IF or IFNOT " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine(), ex);
			}

			appendDebugInfos("else if", ctx);

			//simple if -> expects a boolean
			if (ctx.comparator() == null) {
				compiled.append("} else if (Boolean.TRUE.equals(").append(generateContextQuery(ctx.keyword())).append(")) {\n");
			} //if with a comparator -> 2 
			else {
				if (ctx.comparator().keyword() != null) {
					compiled.append("} else if (ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(generateContextQuery(ctx.comparator().keyword())).append(")) {\n");
				} else if (ctx.comparator().MACRO_STRING() != null) {
					compiled.append("} else if (ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(ctx.comparator().MACRO_STRING()).append(")) {\n");
				} else if (ctx.comparator().MACRO_NUMBER() != null) {
					compiled.append("} else if (ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(ctx.comparator().MACRO_NUMBER()).append(")) {\n");
				}
			}
		}

		@Override
		public void enterExpressionElseIfNot(DLTParser.ExpressionElseIfNotContext ctx)
		{
			try {
				if (structures.peek() != Structure.IF && structures.peek() != Structure.IFNOT) {
					throw new RuntimeException("Error missing start of IF or IFNOT " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
				}
			} catch (EmptyStackException ex) {
				throw new RuntimeException("Error missing start of IF or IFNOT " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine(), ex);
			}

			appendDebugInfos("else ifnot", ctx);

			//simple if -> expects a boolean
			if (ctx.comparator() == null) {
				compiled.append("} else if (!Boolean.TRUE.equals(").append(generateContextQuery(ctx.keyword())).append(")) {\n");
			} //if with a comparator -> 2 
			else {
				if (ctx.comparator().keyword() != null) {
					compiled.append("} else if (!ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(generateContextQuery(ctx.comparator().keyword())).append(")) {\n");
				} else if (ctx.comparator().MACRO_STRING() != null) {
					compiled.append("} else if (!ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(ctx.comparator().MACRO_STRING()).append(")) {\n");
				} else if (ctx.comparator().MACRO_NUMBER() != null) {
					compiled.append("} else if (!ValidationHelper.isEqual(").append(generateContextQuery(ctx.keyword())).append(", ").append(ctx.comparator().MACRO_NUMBER()).append(")) {\n");
				}
			}
		}

		@Override
		public void enterExpressionElse(DLTParser.ExpressionElseContext ctx) throws RuntimeException
		{
			try {
				if (structures.peek() != Structure.IF && structures.peek() != Structure.IFNOT) {
					throw new RuntimeException("Error missing start of IF or IFNOT " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
				}
			} catch (EmptyStackException ex) {
				throw new RuntimeException("Error missing start of IF or IFNOT " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine(), ex);
			}

			appendDebugInfos("else", ctx);

			compiled.append("} else {\n");
		}

		@Override
		public void enterExpressionEndIfNot(DLTParser.ExpressionEndIfNotContext ctx) throws RuntimeException
		{
			try {
				if (structures.pop() != Structure.IFNOT) {
					throw new RuntimeException("Error missing start of IFNOT " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
				}
			} catch (EmptyStackException ex) {
				throw new RuntimeException("Error missing start of IFNOT " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine(), ex);
			}

			appendDebugInfos("end ifnot", ctx);

			compiled.append("}\n");
		}

		@Override
		public void enterExpressionFor(DLTParser.ExpressionForContext ctx)
		{
			appendDebugInfos("for", ctx);

			String forIterator = ctx.forIterator().getText();
			String forIteratorFirst = forIterator + "#first";
			String forIteratorLast = forIterator + "#last";
			String forIteratorIndex = forIterator + "#index";
			String counter = getNextVarName();
			String array = getNextVarName();
			String arrayLength = array + "Length";

			compiled.append("Object[] ").append(array).append(" = (Object[])").append(generateContextQuery(ctx.forArray().keyword())).append(";\n");
			compiled.append("if (").append(array).append(" != null && ").append(array).append(".length > 0) {\n");
			compiled.append("int ").append(arrayLength).append(" = ").append(array).append(".length;\n");
			compiled.append("context.setBinding(\"").append(forIteratorFirst).append("\", true);\n");
			compiled.append("context.setBinding(\"").append(forIteratorLast).append("\", false);\n");
			compiled.append("for (int ").append(counter).append(" = 0; ").append(counter).append(" < ").append(arrayLength).append("; ++").append(counter).append(") {\n");
			compiled.append("context.setBinding(\"").append(forIterator).append("\", ").append(array).append("[").append(counter).append("]);\n");
			compiled.append("context.setBinding(\"").append(forIteratorIndex).append("\", ").append(counter).append(");\n");
			compiled.append("if (").append(counter).append(" < 2) { context.setBinding(\"").append(forIteratorFirst).append("\", (").append(counter).append(" == 0)); }\n");
			compiled.append("if (").append(counter).append(" > ").append(arrayLength).append(" - 2) { context.setBinding(\"").append(forIteratorLast).append("\", (").append(counter).append(" >= ").append(arrayLength).append(" - 1)); }\n");
			structures.push(Structure.FOR);
		}

		@Override
		public void enterExpressionEndFor(DLTParser.ExpressionEndForContext ctx) throws RuntimeException
		{
			try {
				if (structures.pop() != Structure.FOR) {
					throw new RuntimeException("Error missing start of FOR " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
				}
			} catch (EmptyStackException ex) {
				throw new RuntimeException("Error missing start of FOR " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine(), ex);
			}

			appendDebugInfos("end for", ctx);

			compiled.append("}\n}\n");
		}

		@Override
		public void enterExpressionAssert(DLTParser.ExpressionAssertContext ctx)
		{
			appendDebugInfos("assert", ctx);

			compiled
				.append("assert (boolean)")
				.append(generateContextQuery(ctx.keyword()))
				.append(" : ")
				.append(ctx.MACRO_STRING())
				.append(" + \" - \" + ").append(ctx.start.getLine()).append(" + \":\" + ").append(ctx.start.getCharPositionInLine())
				.append((options.getTemplateId() != null) ? " + \" in '" + escapeTextForJavaString(options.getTemplateId()) + "'\"" : "")
				.append(";\n");
		}

		@Override
		public void enterCode(DLTParser.CodeContext ctx)
		{
			appendDebugInfos("code", ctx);

			compiled.append(ctx.getText()).append("\n");
		}

		@Override
		public void enterExpressionLoad(DLTParser.ExpressionLoadContext ctx)
		{
			appendDebugInfos("load", ctx);

			compiled.append("context.load(\"")
				.append(ctx.KEYWORD().getText()).append("\", ")
				.append(ctx.MACRO_STRING().getText()).append(", new Object[] {");

			boolean first = true;
			for (DLTParser.ParameterContext param : ctx.parameter()) {
				if (!first) {
					compiled.append(", ");
				}
				if (param.MACRO_STRING() != null) {
					compiled.append(param.MACRO_STRING());
				} else if (param.MACRO_NUMBER() != null) {
					compiled.append(param.MACRO_NUMBER());
				} else if (param.keyword() != null) {
					compiled.append(generateContextQuery(param.keyword()));
				}
				first = false;
			}

			compiled.append("});\n");
		}

		@Override
		public void enterExpressionSection(DLTParser.ExpressionSectionContext ctx)
		{
			appendDebugInfos("section", ctx);

			compiled.append("context.pushSection(\"").append(ctx.KEYWORD().getText()).append("\");\n");

			structures.push(Structure.SECTION);
		}

		@Override
		public void enterExpressionEndSection(DLTParser.ExpressionEndSectionContext ctx)
		{
			try {
				if (structures.pop() != Structure.SECTION) {
					throw new RuntimeException("Error missing start of SECTION " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
				}
			} catch (EmptyStackException ex) {
				throw new RuntimeException("Error missing start of SECTION " + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine(), ex);
			}

			appendDebugInfos("end section", ctx);

			compiled.append("context.popSection();\n");
		}

		protected String escapeTextForJavaString(String text)
		{
			return text.replace("\r", "").replace("\f", "").replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"");
		}

		protected String generateContextQuery(DLTParser.KeywordContext ctx)
		{
			StringBuilder builder = new StringBuilder();

			//simple case use resolveBinding
			if ((ctx.path().KEYWORD().size() == 1)
				&& (ctx.modifiers().modifier().isEmpty())) {

				builder.append("context.resolveBinding(\"").append(ctx.path().KEYWORD(0)).append("\")");
			} //complex case use resolveBinding
			else {

				builder.append("context.resolveBinding(new String[] {");

				boolean first = true;
				for (TerminalNode n : ctx.path().KEYWORD()) {
					if (!first) {
						builder.append(", ");
					}
					builder.append("\"").append(n.getText()).append("\"");
					first = false;
				}

				builder.append("}, new String[] {");

				first = true;
				for (DLTParser.ModifierContext n : ctx.modifiers().modifier()) {
					if (!first) {
						builder.append(", ");
					}
					builder.append("\"").append(n.getText()).append("\"");
					first = false;
				}

				builder.append("})");
			}

			return builder.toString();
		}
	}

	private static class ErrorListener extends BaseErrorListener
	{

		private final String templateId;

		public ErrorListener(String templateId)
		{
			this.templateId = templateId;
		}

		@Override
		public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int line, int position, String message, RecognitionException re)
		{

			StringBuilder msg = new StringBuilder();
			msg
				.append(message)
				.append("\n\tat ")
				.append(templateId)
				.append("(")
				.append(templateId)
				.append(":")
				.append(line)
				.append(":")
				.append(position)
				.append(")");

			throw new RuntimeException(msg.toString());
		}
	}

	public static String generate(String template, TemplateCompilerOptions options) throws InvalidCompilation
	{
		try {
			DLTLexer lexer = new DLTLexer(CharStreams.fromString(template));
			lexer.removeErrorListeners();
			lexer.addErrorListener(new ErrorListener(options.getTemplateId()));
			TokenStream tokens = new CommonTokenStream(lexer);

			//setup parser
			DLTParser parser = new DLTParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(new ErrorListener(options.getTemplateId()));

			//parse
			DLTParser.WarpTemplateContext context = parser.warpTemplate();
			ParseTreeWalker walker = new ParseTreeWalker();
			ParserListener listener = new ParserListener(options);
			walker.walk(listener, context);

			return listener.getCompiled();
		} catch (RuntimeException ex) {
			throw new InvalidCompilation("Error generating code - " + ex.getMessage(), ex);
		}
	}

	public static <T extends CompiledTemplate> T compile(String template, TemplateCompilerOptions options) throws InvalidCompilation
	{
		assert options != null;
		assert options.getClassLoader() != null;

		T compiled;
		String cacheKey = "";

		if (options.isCacheCompiledTemplate()) {

			cacheKey = "" + options.getTemplateId() + template.hashCode();

			compiled = (T) compiledCache.get(cacheKey);

			if (compiled != null) {

				log.debug("Using cache for {}", cacheKey);

				return compiled;
			}
		}

		compiled = (T) CompileHelper.getCompiledInstance(generate(template, options), options.getPackageName() + "." + options.getClassName(), options.getClassLoader(), options.getClassPath());

		if (options.isCacheCompiledTemplate()) {
			compiledCache.put(cacheKey, compiled);
		}

		return compiled;
	}

	public static void emptyCache()
	{
		compiledCache.clear();
	}
}
