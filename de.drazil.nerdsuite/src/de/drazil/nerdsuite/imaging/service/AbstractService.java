package de.drazil.nerdsuite.imaging.service;

import lombok.Setter;

public abstract class AbstractService implements IService {
	@Setter
	protected String owner = null;
}
