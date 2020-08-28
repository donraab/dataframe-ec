package io.github.vmzakharov.ecdataframe.expr;

import io.github.vmzakharov.ecdataframe.expr.value.Value;
import io.github.vmzakharov.ecdataframe.expr.visitor.ExpressionEvaluationVisitor;
import io.github.vmzakharov.ecdataframe.expr.visitor.ExpressionVisitor;

public class IfElseExpr
implements Expression
{
    private final Expression condition;
    private final Script ifScript;
    private final Script elseScript;

    public IfElseExpr(Expression newCondition, Script newIfScript, Script newElseScript)
    {
        this.condition = newCondition;
        this.ifScript = newIfScript;
        this.elseScript = newElseScript;
    }

    public IfElseExpr(Expression newCondition, Script newIfScript)
    {
        this.condition = newCondition;
        this.ifScript = newIfScript;
        this.elseScript = null;
    }

    public Expression getCondition()
    {
        return this.condition;
    }

    public Script getIfScript()
    {
        return this.ifScript;
    }

    public Script getElseScript()
    {
        return this.elseScript;
    }

    public boolean hasElseSection()
    {
        return this.elseScript != null;
    }

    @Override
    public Value evaluate(ExpressionEvaluationVisitor evaluationVisitor)
    {
        return evaluationVisitor.visitIfElseExpr(this);
    }

    @Override
    public void accept(ExpressionVisitor visitor)
    {
        visitor.visitIfElseExpr(this);
    }
}