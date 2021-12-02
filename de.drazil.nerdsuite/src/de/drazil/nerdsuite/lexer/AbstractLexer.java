package de.drazil.nerdsuite.lexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractLexer {
	private List<Expression> expressionList;

	public AbstractLexer() {
		expressionList = new ArrayList<Expression>();
	}

	public void addExpression(Expression expression) {
		expressionList.add(expression);
	}

	public void removeExpression(Expression expression) {
		expressionList.remove(expression);
	}

	public Collection<Expression> getExpressions() {
		return expressionList;
	}

	public int getExpressionCount() {
		return expressionList.size();
	}
}
