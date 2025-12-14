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

import de.s42.base.conversion.ConversionHelper;

/**
 *
 * @author Benjamin Schiller
 */
public final class NodeBasics
{

	// Node Multiply
	@AsNode(name = "Multiply")
	public static Number multiply(Number value1, Number value2)
	{
		return value1.doubleValue() * value2.doubleValue();
	}

	// Node StaticBoolean
	@AsNode(name = "StaticBoolean")
	public static boolean staticBoolean(boolean value)
	{
		return value;
	}

	// Node StaticFloat
	@AsNode(name = "StaticFloat")
	public static float staticFloat(Double value)
	{
		return ConversionHelper.convert(value, Float.class);
	}

	// Node Floats
	public static class FloatsEmitter implements CodeEmitter
	{

		@Override
		public void emitCode(StringBuilder builder, Node node)
		{
			builder
				.append("\nfloat[] ")
				.append(node.getName())
				.append(" = ")
				.append(node.getNodeClass())
				.append(".")
				.append(node.getNodeMethod())
				.append("(");

			boolean first = true;
			for (Object value : node.getParameters()) {

				if (!first) {
					builder.append(", ");
				}
				first = false;

				if (value instanceof Node nodeValue) {
					builder.append(nodeValue.getName());
				} else {
					builder.append(value);
				}
			}

			builder.append(");");
		}
	}

	@AsNode(name = "Floats", emitter = FloatsEmitter.class)
	public static final float[] convertToFloats(Number... numbers)
	{
		float[] floats = new float[numbers.length];

		for (int i = 0; i < numbers.length; ++i) {
			floats[i] = numbers[i].floatValue();
		}

		return floats;
	}
}
