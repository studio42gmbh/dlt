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

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.types.DLContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class Node implements DLContainer<Node>
{

	protected String name;
	@AttributeDL(defaultValue = "")
	protected Object result = "";
	protected String nodeClass;
	protected String nodeMethod;
	protected Object[] parameters;

	protected final List<Node> children = new ArrayList<>();

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

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Object[] getParameters()
	{
		return parameters;
	}

	public void setParameters(Object[] parameters)
	{
		this.parameters = parameters;
	}

	public String getNodeClass()
	{
		return nodeClass;
	}

	public void setNodeClass(String nodeClass)
	{
		this.nodeClass = nodeClass;
	}

	public String getNodeMethod()
	{
		return nodeMethod;
	}

	public void setNodeMethod(String nodeMethod)
	{
		this.nodeMethod = nodeMethod;
	}

	public Object getResult()
	{
		return result;
	}

	public void setResult(Object result)
	{
		this.result = result;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	// "Getters/Setters" </editor-fold>
}
