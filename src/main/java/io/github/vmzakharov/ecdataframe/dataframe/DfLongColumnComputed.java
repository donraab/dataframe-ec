package io.github.vmzakharov.ecdataframe.dataframe;

import io.github.vmzakharov.ecdataframe.expr.DataFrameEvalContext;
import io.github.vmzakharov.ecdataframe.expr.Expression;
import io.github.vmzakharov.ecdataframe.expr.value.LongValue;
import io.github.vmzakharov.ecdataframe.expr.value.Value;
import io.github.vmzakharov.ecdataframe.expr.visitor.InMemoryEvaluationVisitor;
import io.github.vmzakharov.ecdataframe.util.ExpressionParserHelper;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.impl.list.Interval;

public class DfLongColumnComputed
extends DfLongColumn
implements DfColumnComputed
{
    private final String expressionAsString;
    private final Expression expression;

    public DfLongColumnComputed(DataFrame newDataFrame, String newName, String newExpressionAsString)
    {
        super(newDataFrame, newName);
        this.expressionAsString = newExpressionAsString;
        this.expression = ExpressionParserHelper.toExpression(expressionAsString);
    }

    @Override
    public void addValue(Value value)
    {
        this.unmodifiableColumnException();
    }

    @Override
    public void addObject(Object newObject)
    {
        this.unmodifiableColumnException();
    }

    @Override
    public long getLong(int rowIndex)
    {
        // todo: column in the variable expr or some other optimization?
        DataFrameEvalContext evalContext = this.getDataFrame().getEvalContext();
        evalContext.setRowIndex(rowIndex);

        LongValue result = (LongValue) this.expression.evaluate(new InMemoryEvaluationVisitor(evalContext));
        return result.longValue();
    }

    @Override
    public ImmutableLongList toLongList()
    {
        ImmutableLongList result = Interval
                .zeroTo(this.getDataFrame().rowCount() - 1)
                .collectLong(this::getLong)
                .toList()
                .toImmutable();
        return result;
    }

    @Override
    public long sum()
    {
        return Interval
                .zeroTo(this.getDataFrame().rowCount() - 1)
                .asLazy()
                .collectLong(this::getLong)
                .sum();
    }

    @Override
    public int getSize()
    {
        return this.getDataFrame().rowCount();
    }

    @Override
    public String getExpressionAsString()
    {
        return this.expressionAsString;
    }
}