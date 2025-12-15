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

/**
 *
 * @author Benjamin Schiller
 */
public class ForNode implements CodeEmitter
{

	@AsNode(name = "basic.For", emitter = ForNode.class)
	public static void forNode(Node list, String loopVarName)
	{
		// Code is emitted only via emitter
	}

	@Override
	public void emitCode(StringBuilder builder, Node node) throws Exception
	{
		builder.append("\n\n// ").append(node.getName());

		Node list = (Node) node.getParameters()[0];
		String loopVarName = (String) node.getParameters()[1];

		builder
			.append("\nfor (var ")
			.append(loopVarName)
			.append(" : ")
			.append(list.getName())
			.append(") {");

		NodeModule.emitCode(builder, node.getChildren());

		builder.append("\n}");
	}
}
