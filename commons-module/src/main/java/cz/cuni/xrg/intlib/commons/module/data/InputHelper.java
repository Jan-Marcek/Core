package cz.cuni.xrg.intlib.commons.module.data;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;

/**
 * Support manipulation with input DataUnits.
 * 
 * @author Petyr
 */
@Deprecated
public class InputHelper {

	private InputHelper() {
	}

	/**
	 * Try to get and recast input DataUnit to required type.
	 * 
	 * @param index
	 * @return
	 * @throws MissingInputException
	 */
	public static <T extends DataUnit> T getInput(List<DataUnit> inputs,
			Integer index,
			Class<T> classType) throws MissingInputException {
		if (inputs.size() < index) {
			throw new MissingInputException();
		}
		DataUnit dataUnit = inputs.get(index);

		if (classType.isInstance(dataUnit)) {
			return classType.cast(dataUnit);
		} else {
			throw new ClassCastException();
		}
	}

}
