// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2025 Studio 42 GmbH ( https://www.s42m.de ).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>
package de.s42.dlt.nodes;

import de.s42.base.compile.CompileHelper;
import de.s42.dl.DLInstance;
import de.s42.dl.core.DefaultCore;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class NodeModuleTest
{

	private final static Logger log = LogManager.getLogger(NodeModuleTest.class.getName());

	@Test
	public void testSimpleModule() throws Exception
	{
		log.start("testSimpleModule");

		DefaultCore core = new DefaultCore();

		core.defineType(Node.class, "Node");
		core.defineType(NodeModule.class, "NodeModule");
		core.definePragma(new LoadNodeClassPragma());
		core.setAllowDefinePragmas(false);
		core.doPragma("loadNodeClass", BasicNodes.class.getName());
		core.doPragma("loadNodeClass", ReturnNode.class.getName());
		core.doPragma("loadNodeClass", IfNode.class.getName());
		core.doPragma("loadNodeClass", ForNode.class.getName());
		core.setAllowUsePragmas(false);

		DLInstance nodeModuleInstance = core.parse("de/s42/dlt/nodes/Test.nodes.dl");

		NodeModule nodeModule = (NodeModule) nodeModuleInstance.getChildAsJavaObject(0);

		String code = nodeModule.emitCode();

		log.info("Java Code:\n\n" + code + "\n");

		Class<CompiledNodeModule> compiledNodeModuleClass = CompileHelper.getCompiledClass(code, "MyNodes");

		CompiledNodeModule compiledNodeModule = compiledNodeModuleClass.getConstructor().newInstance();

		log.info("Result:", compiledNodeModule.execute("Test"));

		log.stopDebug("testSimpleModule");
	}
}
