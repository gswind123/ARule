package org.windning.arule.bridge.model;

import org.windning.arule.bridge.exception.InvalidNativeValueException;

public interface NativeField {
	Object getValue() throws InvalidNativeValueException;
}
