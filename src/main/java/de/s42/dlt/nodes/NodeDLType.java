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

import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLType;
import de.s42.dl.attributes.DefaultDLAttribute;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.exceptions.InvalidType;
import de.s42.dl.types.CustomConvert;
import de.s42.dl.types.DefaultDLType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class NodeDLType extends DefaultDLType implements CustomConvert
{

	protected final String nodeClass;
	protected final String methodName;
	protected final List<Parameter> parameters;

	public NodeDLType(Method nodeMethod, DLCore core) throws InvalidType
	{
		this.nodeClass = nodeMethod.getDeclaringClass().getName();
		this.methodName = nodeMethod.getName();

		// Get AsNode annotation
		AsNode asNode = nodeMethod.getAnnotation(AsNode.class);

		// Name is either class name plus methode name or the custom name from AsNode.name
		String typeName = nodeClass + "." + methodName;

		if (asNode != null
			&& !asNode.name().isBlank()) {
			typeName = asNode.name();
		}

		setName(typeName);

		// Parse the given parameters and add them
		for (Parameter parameter : nodeMethod.getParameters()) {

			DLType parameterType = core.getType(parameter.getType()).orElseThrow();

			DefaultDLAttribute attribute = new DefaultDLAttribute(parameter.getName(), parameterType, this);

			addAttribute(attribute);
		}
		parameters = new ArrayList<>(Arrays.asList(nodeMethod.getParameters()));

		// Make Node a virtual parent of this node
		DLType nClass = core.getType(Node.class).orElseThrow();
		addParent(nClass);
	}

	@Override
	public Object convertFromInstance(DLInstance instance) throws InvalidInstance
	{
		Node node = new Node();
		node.setName((instance.getName() != null) ? instance.getName() : getName());
		node.setNodeClass(nodeClass);
		node.setNodeMethod(methodName);

		Object[] params = new Object[parameters.size()];
		for (int p = 0; p < params.length; ++p) {

			//Parameter parameter = parameters.get(p);
			Object parameterValue = instance.get("arg" + p);

			switch (parameterValue) {
				case Object[] objects -> {

					Object[] convValue = new Object[objects.length];

					for (int i = 0; i < ((Object[]) objects).length; ++i) {

						Object val = objects[i];

						if (val instanceof DLInstance dLInstance) {
							convValue[i] = dLInstance.toJavaObject();
						} else {
							convValue[i] = val;
						}
					}

					parameterValue = convValue;
				}
				case DLInstance dLInstance ->
					parameterValue = dLInstance.toJavaObject();
				default -> {
				}
			}

			params[p] = parameterValue;
		}
		node.setParameters(params);

		if (instance.hasChildren()) {

			for (DLInstance child : instance.getChildren()) {

				Node convertChild = (Node) child.toJavaObject();

				node.addChild(child.getName(), convertChild);
			}
		}

		return node;
	}
}
