package de.drazil.nerdsuite.lexer;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Token {
	@Getter
	@Setter
	private Type type;
	@Getter
	@Setter
	private String content;
	@Getter
	@Setter
	private int start;
	@Getter
	@Setter
	private int end;
	@Getter
	private List<Token> tokenList;

	public Token(Type type, String content,int start,int end) {
		this.type = type;
		this.content = content;
		this.start=start;
		this.end=end;
		this.tokenList = new ArrayList<Token>();
	}

	public void addToken(Token token) {
		tokenList.add(token);
	}

	public void removeToken(Token token) {
		tokenList.remove(token);
	}

	public void removeToken(int index) {
		tokenList.remove(index);
	}
}
