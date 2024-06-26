package io.github.vmzakharov.ecdataframe.dataframe.aggregation;

import io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction;
import io.github.vmzakharov.ecdataframe.dataframe.DfDecimalColumn;
import io.github.vmzakharov.ecdataframe.dataframe.DfDoubleColumn;
import io.github.vmzakharov.ecdataframe.dataframe.DfFloatColumn;
import io.github.vmzakharov.ecdataframe.dataframe.DfIntColumn;
import io.github.vmzakharov.ecdataframe.dataframe.DfLongColumn;
import io.github.vmzakharov.ecdataframe.dataframe.DfObjectColumn;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.factory.Lists;

import java.math.BigDecimal;

import static io.github.vmzakharov.ecdataframe.dsl.value.ValueType.DECIMAL;
import static io.github.vmzakharov.ecdataframe.dsl.value.ValueType.DOUBLE;
import static io.github.vmzakharov.ecdataframe.dsl.value.ValueType.FLOAT;
import static io.github.vmzakharov.ecdataframe.dsl.value.ValueType.INT;
import static io.github.vmzakharov.ecdataframe.dsl.value.ValueType.LONG;

public class Min
extends AggregateFunction
{
    private static final ListIterable<ValueType> SUPPORTED_TYPES = Lists.immutable.of(INT, LONG, DOUBLE, FLOAT, DECIMAL);

    public Min(String newColumnName)
    {
        super(newColumnName);
    }

    public Min(String newColumnName, String newTargetColumnName)
    {
        super(newColumnName, newTargetColumnName);
    }

    @Override
    public ListIterable<ValueType> supportedSourceTypes()
    {
        return SUPPORTED_TYPES;
    }

    @Override
    public String getDescription()
    {
        return "Minimum numeric value";
    }

    @Override
    public Object applyToDoubleColumn(DfDoubleColumn doubleColumn)
    {
        return doubleColumn.asDoubleIterable().min();
    }

    @Override
    public Object applyToFloatColumn(DfFloatColumn floatColumn)
    {
        return floatColumn.asFloatIterable().min();
    }

    @Override
    public Object applyToLongColumn(DfLongColumn longColumn)
    {
        return longColumn.asLongIterable().min();
    }

    @Override
    public Object applyToIntColumn(DfIntColumn intColumn)
    {
        return intColumn.asIntIterable().min();
    }

    @Override
    public Object applyToObjectColumn(DfObjectColumn<?> objectColumn)
    {
        return ((DfDecimalColumn) objectColumn).injectIntoBreakOnNulls(
                this.objectInitialValue(),
                (result, current) -> result.compareTo(current) < 0 ? result : current
        );
    }

    @Override
    public int intInitialValue()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public long longInitialValue()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public double doubleInitialValue()
    {
        return Double.MAX_VALUE;
    }

    @Override
    public float floatInitialValue()
    {
        return Float.MAX_VALUE;
    }

    @Override
    public BigDecimal objectInitialValue()
    {
        return BigDecimal.valueOf(Double.MAX_VALUE);
    }

    @Override
    protected long longAccumulator(long currentAggregate, long newValue)
    {
        return Math.min(currentAggregate, newValue);
    }

    @Override
    protected double doubleAccumulator(double currentAggregate, double newValue)
    {
        return Math.min(currentAggregate, newValue);
    }

    @Override
    protected float floatAccumulator(float currentAggregate, float newValue)
    {
        return Math.min(currentAggregate, newValue);
    }

    @Override
    protected int intAccumulator(int currentAggregate, int newValue)
    {
        return Math.min(currentAggregate, newValue);
    }

    @Override
    protected Object objectAccumulator(Object currentAggregate, Object newValue)
    {
        return ((BigDecimal) currentAggregate).compareTo((BigDecimal) newValue) > 0 ? newValue : currentAggregate;
    }
}
