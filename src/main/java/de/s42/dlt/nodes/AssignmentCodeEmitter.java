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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class AssignmentCodeEmitter implements CodeEmitter
{

	@Override
	public void emitCode(StringBuilder builder, Node node) throws Exception
	{
		Class nodeClass = Class.forName(node.getNodeClass());

		List<Class> parameterTypes = new ArrayList<>();

		for (Object parameter : node.getParameters()) {
			parameterTypes.add(parameter.getClass());
		}

		builder.append("\n");

		Method method = NodeModule.findMethod(nodeClass, node.getNodeMethod(), parameterTypes);

		if (method.getReturnType() != void.class) {
			builder
				.append(method.getReturnType().getName())
				.append(" ")
				.append(node.getName())
				.append(" = ");
		}

		builder
			.append(nodeClass.getName())
			.append(".")
			.append(method.getName())
			.append("(");

		// <parameter>, ...
		boolean first = true;
		for (Object parameter : node.parameters) {

			if (!first) {
				builder.append(", ");
			}
			first = false;

			if (parameter instanceof Node pN) {
				builder.append(pN.getName());
				continue;
			}

			builder.append(parameter);
		}

		builder.append(");");
	}
}
