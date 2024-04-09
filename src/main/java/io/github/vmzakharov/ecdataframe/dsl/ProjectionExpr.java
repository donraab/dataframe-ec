package io.github.vmzakharov.ecdataframe.dsl;

import io.github.vmzakharov.ecdataframe.dsl.value.Value;
import io.github.vmzakharov.ecdataframe.dsl.visitor.ExpressionEvaluationVisitor;
import io.github.vmzakharov.ecdataframe.dsl.visitor.ExpressionVisitor;
import io.github.vmzakharov.ecdataframe.dsl.visitor.PrettyPrintVisitor;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public class ProjectionExpr
implements Expression
{
    private final Expression whereClause;
    private final ListIterable<Expression> projectionElements;
    private final MutableList<String> elementNames = Lists.mutable.of();
    private final MutableList<Expression> projectionExpressions = Lists.mutable.of();

    public ProjectionExpr(ListIterable<Expression> newProjectionElements)
    {
        this(newProjectionElements, null);
    }

    public ProjectionExpr(ListIterable<Expression> newProjectionElements, Expression newWhereClause)
    {
        this.projectionElements = newProjectionElements;
        this.whereClause = newWhereClause;

        for (int i = 0; i < this.projectionElements.size(); i++)
        {
            Expression element = this.projectionElements.get(i);
            if (element instanceof AliasExpr aliasExpr)
            {
                this.elementNames.add(aliasExpr.getAlias());
                this.projectionExpressions.add(aliasExpr.getExpression());
            }
            else
            {
                this.elementNames.add(PrettyPrintVisitor.exprToString(element));
                this.projectionExpressions.add(element);
            }
        }
    }

    @Override
    public Value evaluate(ExpressionEvaluationVisitor visitor)
    {
        return visitor.visitProjectionExpr(this);
    }

    @Override
    public void accept(ExpressionVisitor visitor)
    {
        visitor.visitProjectionExpr(this);
    }

    public Expression getWhereClause()
    {
        return this.whereClause;
    }

    public ListIterable<Expression> getProjectionElements()
    {
        return this.projectionElements;
    }

    public ListIterable<Expression> getProjectionExpressions()
    {
        return this.projectionExpressions;
    }

    public ListIterable<String> getElementNames()
    {
        return this.elementNames;
    }
}
