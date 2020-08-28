package io.github.vmzakharov.ecdataframe.expr;

import io.github.vmzakharov.ecdataframe.expr.value.Value;
import io.github.vmzakharov.ecdataframe.expr.visitor.ExpressionEvaluationVisitor;
import io.github.vmzakharov.ecdataframe.expr.visitor.ExpressionVisitor;

public class AliasExpr
implements Expression
{
    private String alias;
    private Expression expression;

    public AliasExpr(String newAlias, Expression newExpression)
    {
        this.alias = newAlias;
        this.expression = newExpression;
    }

    @Override
    public Value evaluate(ExpressionEvaluationVisitor evaluationVisitor)
    {
        return this.expression.evaluate(evaluationVisitor);
    }

    @Override
    public void accept(ExpressionVisitor visitor)
    {
        visitor.visitAliasExpr(this);
    }

    public String getAlias()
    {
        return this.alias;
    }

    public Expression getExpression()
    {
        return this.expression;
    }
}