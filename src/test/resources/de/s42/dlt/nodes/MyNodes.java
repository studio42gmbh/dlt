package de.s42.dlt.nodes;

public class MyNodes implements de.s42.dlt.nodes.CompiledNodeModule
{

	@Override
	public Object execute(Object context)
	{

		boolean config1 = de.s42.dlt.nodes.BasicNodes.StaticBoolean(true);

		if (config1) {

			boolean config2 = de.s42.dlt.nodes.BasicNodes.StaticBoolean(false);

			if (config2) {

				java.lang.Number mul1 = de.s42.dlt.nodes.BasicNodes.Multiply(14.0, 6.23);

				if (true) {
					return mul1;
				}
			}
		}
		float node1 = de.s42.dlt.nodes.BasicNodes.StaticFloat(5.0);
		float[] numbers1 = de.s42.dlt.nodes.BasicNodes.Floats(1.0, 2.0, node1);

// For
		for (var numberC : numbers1) {
			java.lang.Number mulFor = de.s42.dlt.nodes.BasicNodes.Multiply(2.34, 1.23);
		}

		if (true) {
			return node1;
		}

// Expects earlier returns to be guarded - is as fallback if nothing is returned before
		return null;
	}
}
