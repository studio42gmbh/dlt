package de.s42.dlt.nodes;

public class MyNodes implements de.s42.dlt.nodes.CompiledNodeModule
{

	@Override
	public Object execute(Object context)
	{

		boolean config1 = true;

		if (config1) {

			boolean config2 = false;

			if (config2) {

				java.lang.Number mul1 = de.s42.dlt.nodes.NodeBasics.multiply(7.0, 6.23);

				if (true) {
					return mul1;
				}
			}
		}
		float node1 = de.s42.dlt.nodes.NodeBasics.staticFloat(5.0);
		float[] numbers1 = de.s42.dlt.nodes.NodeBasics.convertToFloats(1.0, 2.0, node1);

		if (true) {
			return numbers1;
		}

		// Expects earlier returns to be guarded - is as fallback if nothing is returned before
		return null;
	}
}
