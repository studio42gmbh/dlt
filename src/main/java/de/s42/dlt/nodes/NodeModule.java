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

import de.s42.dl.types.DLContainer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class NodeModule implements DLContainer<Node>
{

	protected final List<Node> children = new ArrayList<>();

	public final static CodeEmitter DEFAULT_CODE_EMITTER = new AssignmentCodeEmitter();

	@Override
	public void addChild(String name, Node child)
	{
		children.add(child);
	}

	@Override
	public List<Node> getChildren()
	{
		return Collections.unmodifiableList(children);
	}

	public String emitCode() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append(
			"""
			public class MyNodes implements de.s42.dlt.nodes.CompiledNodeModule
			{

			@Override
			public Object execute(Object context)
			{
			"""
		);

		emitCode(builder, children);

		builder.append(
			"""


			// Expects earlier returns to be guarded - is as fallback if nothing is returned before
			return null;
			}
			}

			""");
		return builder.toString();
	}

	public static void emitCode(StringBuilder builder, List<Node> children) throws Exception
	{
		for (Node node : children) {
			emitCode(builder, node);
		}
	}

	public static void emitCode(StringBuilder builder, Node node) throws Exception
	{
		Class nodeClass = Class.forName(node.getNodeClass());

		List<Class> parameterTypes = new ArrayList<>();

		for (Object parameter : node.getParameters()) {
			parameterTypes.add(parameter.getClass());
		}

		Method method = findMethod(nodeClass, node.getNodeMethod(), parameterTypes);

		if (method == null) {
			throw new RuntimeException("Method " + node.getNodeMethod() + " not found");
		}

		CodeEmitter emitter = DEFAULT_CODE_EMITTER;

		// Handle annotation asNode
		AsNode asNode = method.getAnnotation(AsNode.class);
		if (asNode != null) {

			// Emit code through annotation emitter
			if (!asNode.emitter().isInterface()) {

				emitter = asNode.emitter().getConstructor().newInstance();
			}
		}

		emitter.emitCode(builder, node);
	}

	public static Method findMethod(Class type, String methodName, List<Class> parameterTypes) throws NoSuchMethodException
	{
		//log.debug("findMethod", type.getName(), methodName, parameterTypes);

		// Search all methods of this class
		for (Method method : type.getDeclaredMethods()) {

			// Is the method public static
			if (method.getName().equals(methodName)
				&& Modifier.isStatic(method.getModifiers())) {

				// Parameters match
				if (method.getParameterCount() == parameterTypes.size()) {
					return method;
				}

				Parameter[] parameters = method.getParameters();

				// First parameter is an array
				if (parameters.length == 1 && parameters[0].getType().isArray()) {
					return method;
				}
			}
		}

		return null;
	}
}
