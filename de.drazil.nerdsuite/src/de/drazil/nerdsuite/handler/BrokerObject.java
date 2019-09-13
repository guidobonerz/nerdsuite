package de.drazil.nerdsuite.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrokerObject {
	private String owner;
	private Object transferObject;
}
