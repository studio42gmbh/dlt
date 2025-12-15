// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2022 Studio 42 GmbH ( https://www.s42m.de ).
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
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidPragma;
import de.s42.dl.pragmas.AbstractDLPragma;
import de.s42.dl.util.DLHelper;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author Benjamin Schiller
 */
public class LoadNodeClassPragma extends AbstractDLPragma
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(LoadNodeClassPragma.class.getName());

	public final static String DEFAULT_IDENTIFIER = "loadNodeClass";

	public LoadNodeClassPragma()
	{
		super(DEFAULT_IDENTIFIER);
	}

	public LoadNodeClassPragma(String identifier)
	{
		super(identifier);
	}

	@Override
	public void doPragma(DLCore core, Object... parameters) throws InvalidPragma
	{
		log.trace("doPragma");

		parameters = validateParameters(parameters, new Class[]{Class.class});

		final Class nodeClass = (Class) parameters[0];

		log.trace("doPragma:foundClass", nodeClass.getName());

		// Find public static methods in this class
		for (Method method : nodeClass.getDeclaredMethods()) {

			if (Modifier.isStatic(method.getModifiers())
				&& Modifier.isPublic(method.getModifiers())) {

				log.trace("doPragma:foundMethod", method.getName());

				// Construct virtual DLType
				try {

					NodeDLType nodeType = new NodeDLType(method, core);

					log.trace(DLHelper.describe(nodeType));

					// Register the new virtual type
					core.defineType(nodeType);

				} catch (DLException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
}
