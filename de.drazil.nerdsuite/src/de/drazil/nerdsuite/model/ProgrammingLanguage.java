package de.drazil.nerdsuite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class ProgrammingLanguage {
	private String id;
	private String name;

	private int version;
	@JsonInclude(Include.NON_ABSENT)
	private boolean supportsExampleFile = false;
	@JsonInclude(Include.NON_ABSENT)
	private List<SimpleEntity> builder;
}
