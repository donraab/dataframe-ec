package io.github.vmzakharov.ecdataframe.dataframe;

import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import org.eclipse.collections.api.list.ListIterable;

public abstract class AggregateFunction
{
    private final String columnName;
    private final String targetColumnName;

    public AggregateFunction(String newColumnName)
    {
        this(newColumnName, newColumnName);
    }

    public AggregateFunction(String newColumnName, String newTargetColumnName)
    {
        this.columnName = newColumnName;
        this.targetColumnName = newTargetColumnName;
    }

    public static AggregateFunction sum(String newColumnName)
    {
        return new Sum(newColumnName);
    }

    public static AggregateFunction sum(String newColumnName, String newTargetColumnName)
    {
        return new Sum(newColumnName, newTargetColumnName);
    }

    public static AggregateFunction min(String newColumnName)
    {
        return new Min(newColumnName);
    }

    public static AggregateFunction min(String newColumnName, String newTargetColumnName)
    {
        return new Min(newColumnName, newTargetColumnName);
    }

    public static AggregateFunction max(String newColumnName)
    {
        return new Max(newColumnName);
    }

    public static AggregateFunction max(String newColumnName, String newTargetColumnName)
    {
        return new Max(newColumnName, newTargetColumnName);
    }

    public static AggregateFunction avg(String newColumnName)
    {
        return new Avg(newColumnName);
    }

    public static AggregateFunction avg(String newColumnName, String newTargetColumnName)
    {
        return new Avg(newColumnName, newTargetColumnName);
    }

    public static AggregateFunction count(String newColumnName)
    {
        return new Count(newColumnName);
    }

    public static AggregateFunction count(String newColumnName, String newTargetColumnName)
    {
        return new Count(newColumnName, newTargetColumnName);
    }

    public static AggregateFunction same(String newColumnName)
    {
        return new Same(newColumnName);
    }

    public static AggregateFunction same(String newColumnName, String newTargetColumnName)
    {
        return new Same(newColumnName, newTargetColumnName);
    }

    public String getColumnName()
    {
        return this.columnName;
    }

    public String getTargetColumnName()
    {
        return this.targetColumnName;
    }

    public ValueType targetColumnType(ValueType sourceColumnType)
    {
        return sourceColumnType;
    }

    public Object applyToDoubleColumn(DfDoubleColumn doubleColumn)
    {
        this.throwNotApplicable("double values");
        return 0.0;
    }

    public Object applyToLongColumn(DfLongColumn longColumn)
    {
        this.throwNotApplicable("long values");
        return 0;
    }

    public Object applyToObjectColumn(DfObjectColumn<?> objectColumn)
    {
        if (this.handlesObjectIterables())
        {
            this.applyIterable(objectColumn.toList());
        }
        else
        {
            this.throwNotApplicable("non-numeric values");
        }
        return null;
    }

    public Object applyIterable(ListIterable<?> items)
    {
        this.throwNotApplicable("non-numeric values");
        return null;
    }

    private void throwNotApplicable(String scope)
    {
        ErrorReporter.unsupported(
                "Aggregation '" + this.getName() + "' (" + this.getDescription() + ") cannot be performed on " + scope);
    }

    long longInitialValue()
    {
        ErrorReporter.unsupported("Operation " + this.getDescription() + " does not have a long initial value");
        return 0;
    }

    double doubleInitialValue()
    {
        ErrorReporter.unsupported("Operation " + this.getDescription() + " does not have a double initial value");
        return 0.0;
    }

    Object objectInitialValue()
    {
        ErrorReporter.unsupported("Operation " + this.getDescription() + " does not have a non-numeric initial value");
        return null;
    }

    protected long longAccumulator(long currentAggregate, long newValue)
    {
        ErrorReporter.unsupported("Operation " + this.getDescription() + " does not support a long accumulator");
        return 0;
    }

    protected double doubleAccumulator(double currentAggregate, double newValue)
    {
        ErrorReporter.unsupported("Operation " + this.getDescription() + " does not support a double accumulator");
        return 0.0;
    }

    protected Object objectAccumulator(Object currentAggregate, Object newValue)
    {
        ErrorReporter.unsupported("Operation " + this.getDescription() + " does not support an non-numeric accumulator");
        return null;
    }

    /**
     * A short name used to identify this aggregation function (such as 'Sum', 'Min', 'Avg', etc.). It is primarily
     * meant for internal used (as opposed to the value returned by the <code>getDescription()</code> method).
     * @return the name of this aggregation function
     */
    public String getName()
    {
        return this.getClass().getSimpleName();
    }

    /**
     * A brief description of the aggregation operation supported by this function (primarily for display purposes)
     * @return the description of this aggregation function
     */
    public String getDescription()
    {
        return this.getName();
    }

    public Object defaultObjectIfEmpty()
    {
        this.throwNotApplicable("empty lists");
        return null;
    }

    public long defaultLongIfEmpty()
    {
        this.throwNotApplicable("empty lists");
        return 0;
    }

    public double defaultDoubleIfEmpty()
    {
        this.throwNotApplicable("empty lists");
        return 0.0;
    }

    public long getLongValue(DfColumn sourceColumn, int sourceRowIndex)
    {
        return ((DfLongColumn) sourceColumn).getLong(sourceRowIndex);
    }

    public double getDoubleValue(DfColumn sourceColumn, int sourceRowIndex)
    {
        return ((DfDoubleColumn) sourceColumn).getDouble(sourceRowIndex);
    }

    public Object getObjectValue(DfColumn sourceColumn, int sourceRowIndex)
    {
        return sourceColumn.getObject(sourceRowIndex);
    }

    public void finishAggregating(DataFrame aggregatedDataFrame, int[] countsByRow)
    {
    }

    public void initializeValue(DfColumn accumulatorColumn, int accumulatorRowIndex)
    {
        if (accumulatorColumn.getType().isDouble())
        {
            ((DfDoubleColumnStored) accumulatorColumn).setDouble(accumulatorRowIndex, this.doubleInitialValue());
        }
        else if (accumulatorColumn.getType().isLong())
        {
            ((DfLongColumnStored) accumulatorColumn).setLong(accumulatorRowIndex, this.longInitialValue());
        }
        else
        {
            accumulatorColumn.setObject(accumulatorRowIndex, this.objectInitialValue());
        }
    }

    public void aggregateValueIntoLong(
            DfLongColumnStored targetColumn, int targetRowIndex,
            DfColumn sourceColumn, int sourceRowIndex)
    {
        long currentAggregatedValue = targetColumn.getLong(targetRowIndex);
        targetColumn.setLong(
                targetRowIndex,
                this.longAccumulator(currentAggregatedValue, this.getLongValue(sourceColumn, sourceRowIndex)));
    }

    public void aggregateValueIntoDouble(
            DfDoubleColumnStored targetColumn, int targetRowIndex,
            DfColumn sourceColumn, int sourceRowIndex)
    {
        double currentAggregatedValue = targetColumn.getDouble(targetRowIndex);
        targetColumn.setDouble(
                targetRowIndex,
                this.doubleAccumulator(currentAggregatedValue, this.getDoubleValue(sourceColumn, sourceRowIndex)));
    }

    public boolean handlesObjectIterables()
    {
        return false;
    }

    /**
     * by default aggregators treat null values as "poisonous" - that is any null value passed in the aggregator will
     * cause the result of the entire aggregation to be null, which is a sensible behavior for most aggregation
     * functions.
     *
     * Override this method to return <code>false</code> if this aggregation function can handle null value.
     *
     * @return <code>true</code> if nulls are poisonous, <code>false</code> if nulls can be handled
     */
    public boolean nullsArePoisonous()
    {
        return true;
    }

    public static class Sum
    extends AggregateFunction
    {
        public Sum(String newColumnName)
        {
            super(newColumnName);
        }

        public Sum(String newColumnName, String newTargetColumnName)
        {
            super(newColumnName, newTargetColumnName);
        }

        @Override
        public String getDescription()
        {
            return "Sum";
        }

        @Override
        public Object applyToDoubleColumn(DfDoubleColumn doubleColumn)
        {
            return doubleColumn.toDoubleList().sum();
        }

        @Override
        public Object applyToLongColumn(DfLongColumn longColumn)
        {
            return longColumn.toLongList().sum();
        }

        @Override
        long longInitialValue()
        {
            return 0L;
        }

        @Override
        double doubleInitialValue()
        {
            return 0.0;
        }

        @Override
        protected long longAccumulator(long currentAggregate, long newValue)
        {
            return currentAggregate + newValue;
        }

        @Override
        protected double doubleAccumulator(double currentAggregate, double newValue)
        {
            return currentAggregate + newValue;
        }

        @Override
        public long defaultLongIfEmpty()
        {
            return 0L;
        }

        @Override
        public double defaultDoubleIfEmpty()
        {
            return 0.0;
        }
    }

    public static class Max
    extends AggregateFunction
    {
        public Max(String newColumnName)
        {
            super(newColumnName);
        }

        public Max(String newColumnName, String newTargetColumnName)
        {
            super(newColumnName, newTargetColumnName);
        }

        @Override
        public String getDescription()
        {
            return "Maximum numeric value";
        }

        @Override
        public Object applyToDoubleColumn(DfDoubleColumn doubleColumn)
        {
            return doubleColumn.toDoubleList().max();
        }

        @Override
        public Object applyToLongColumn(DfLongColumn longColumn)
        {
            return longColumn.toLongList().max();
        }

        @Override
        long longInitialValue()
        {
            return Long.MIN_VALUE;
        }

        @Override
        double doubleInitialValue()
        {
            return -Double.MAX_VALUE;
        }

        @Override
        protected long longAccumulator(long currentAggregate, long newValue)
        {
            return Math.max(currentAggregate, newValue);
        }

        @Override
        protected double doubleAccumulator(double currentAggregate, double newValue)
        {
            return Math.max(currentAggregate, newValue);
        }
    }

    public static class Min
    extends AggregateFunction
    {
        public Min(String newColumnName)
        {
            super(newColumnName);
        }

        public Min(String newColumnName, String newTargetColumnName)
        {
            super(newColumnName, newTargetColumnName);
        }

        @Override
        public String getDescription()
        {
            return "Minimum numeric value";
        }

        @Override
        public Object applyToDoubleColumn(DfDoubleColumn doubleColumn)
        {
            return doubleColumn.toDoubleList().min();
        }

        @Override
        public Object applyToLongColumn(DfLongColumn longColumn)
        {
            return longColumn.toLongList().min();
        }

        @Override
        long longInitialValue()
        {
            return Long.MAX_VALUE;
        }

        @Override
        double doubleInitialValue()
        {
            return Double.MAX_VALUE;
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
    }

    public static class Avg
    extends AggregateFunction
    {
        public Avg(String newColumnName)
        {
            super(newColumnName);
        }

        public Avg(String newColumnName, String newTargetColumnName)
        {
            super(newColumnName, newTargetColumnName);
        }

        @Override
        public String getDescription()
        {
            return "Average, or mean of numeric values";
        }

        @Override
        public Object applyToDoubleColumn(DfDoubleColumn doubleColumn)
        {
            return doubleColumn.toDoubleList().average();
        }

        @Override
        public Object applyToLongColumn(DfLongColumn longColumn)
        {
            return Math.round(longColumn.toLongList().average());
        }

        @Override
        protected long longAccumulator(long currentAggregate, long newValue)
        {
            return currentAggregate + newValue;
        }

        @Override
        protected double doubleAccumulator(double currentAggregate, double newValue)
        {
            return currentAggregate + newValue;
        }

        @Override
        long longInitialValue()
        {
            return 0;
        }

        @Override
        double doubleInitialValue()
        {
            return 0;
        }

        @Override
        public void finishAggregating(DataFrame aggregatedDataFrame, int[] countsByRow)
        {
            DfColumn aggregatedColumn = aggregatedDataFrame.getColumnNamed(this.getTargetColumnName());

            if (aggregatedColumn.getType().isLong())
            {
                DfLongColumnStored longColumn = (DfLongColumnStored) aggregatedColumn;

                int columnSize = longColumn.getSize();
                for (int rowIndex = 0; rowIndex < columnSize; rowIndex++)
                {
                    // todo: check for null
                    longColumn.setLong(rowIndex, longColumn.getLong(rowIndex) / countsByRow[rowIndex]);
                }
            }
            else if (aggregatedColumn.getType().isDouble())
            {
                DfDoubleColumnStored doubleColumn = (DfDoubleColumnStored) aggregatedColumn;

                int columnSize = doubleColumn.getSize();
                for (int rowIndex = 0; rowIndex < columnSize; rowIndex++)
                {
                    // todo: check for null
                    doubleColumn.setDouble(rowIndex, doubleColumn.getDouble(rowIndex) / countsByRow[rowIndex]);
                }
            }
        }
    }

    public static class Count
    extends AggregateFunction
    {
        public Count(String newColumnName)
        {
            super(newColumnName);
        }

        public Count(String newColumnName, String newTargetColumnName)
        {
            super(newColumnName, newTargetColumnName);
        }

        @Override
        public ValueType targetColumnType(ValueType sourceColumnType)
        {
            return ValueType.LONG;
        }

        @Override
        public String getDescription()
        {
            return "Count the number of rows in a set or a group within a set";
        }

        @Override
        public Object applyToDoubleColumn(DfDoubleColumn doubleColumn)
        {
            return doubleColumn.getSize();
        }

        @Override
        public Object applyToLongColumn(DfLongColumn longColumn)
        {
            return longColumn.getSize();
        }

        @Override
        public Object applyToObjectColumn(DfObjectColumn<?> objectColumn)
        {
            return objectColumn.getSize();
        }

        @Override
        public long getLongValue(DfColumn sourceColumn, int sourceRowIndex)
        {
            return 0;
        }

        @Override
        public double getDoubleValue(DfColumn sourceColumn, int sourceRowIndex)
        {
            return 0.0;
        }

        @Override
        protected long longAccumulator(long currentAggregate, long newValue)
        {
            return currentAggregate + 1;
        }

        @Override
        protected double doubleAccumulator(double currentAggregate, double newValue)
        {
            return currentAggregate + 1;
        }

        @Override
        long longInitialValue()
        {
            return 0;
        }

        @Override
        double doubleInitialValue()
        {
            return 0;
        }

        @Override
        public long defaultLongIfEmpty()
        {
            return 0L;
        }

        @Override
        public double defaultDoubleIfEmpty()
        {
            return 0.0;
        }
    }

    public static class Same
    extends AggregateFunction
    {
        private static final long INITIAL_VALUE_LONG = System.nanoTime();
        private static final double INITIAL_VALUE_DOUBLE = INITIAL_VALUE_LONG;
        private static final Object INITIAL_VALUE_OBJECT = new Object();

        public Same(String newColumnName)
        {
            super(newColumnName);
        }

        public Same(String newColumnName, String newTargetColumnName)
        {
            super(newColumnName, newTargetColumnName);
        }

        @Override
        long longInitialValue()
        {
            return INITIAL_VALUE_LONG;
        }

        @Override
        double doubleInitialValue()
        {
            return INITIAL_VALUE_DOUBLE;
        }

        @Override
        Object objectInitialValue()
        {
            return INITIAL_VALUE_OBJECT;
        }

        @Override
        public boolean handlesObjectIterables()
        {
            return true;
        }

        @Override
        public void aggregateValueIntoLong(DfLongColumnStored targetColumn, int targetRowIndex, DfColumn sourceColumn, int sourceRowIndex)
        {
            long currentAggregatedValue = targetColumn.getLong(targetRowIndex);
            long nextValue = this.getLongValue(sourceColumn, sourceRowIndex);

            if (currentAggregatedValue == INITIAL_VALUE_LONG)
            {
                targetColumn.setObject(targetRowIndex, nextValue);
            }
            else if (currentAggregatedValue != nextValue)
            {
                targetColumn.setObject(targetRowIndex, null);
            }
        }

        @Override
        public void aggregateValueIntoDouble(DfDoubleColumnStored targetColumn, int targetRowIndex, DfColumn sourceColumn, int sourceRowIndex)
        {
            double currentAggregatedValue = targetColumn.getDouble(targetRowIndex);
            double nextValue = this.getDoubleValue(sourceColumn, sourceRowIndex);

            if (currentAggregatedValue == INITIAL_VALUE_DOUBLE)
            {
                targetColumn.setObject(targetRowIndex, nextValue);
            }
            else if (currentAggregatedValue != nextValue)
            {
                targetColumn.setObject(targetRowIndex, null);
            }
        }

        @Override
        protected Object objectAccumulator(Object currentAggregate, Object newValue)
        {
            if (currentAggregate == INITIAL_VALUE_OBJECT)
            {
                return newValue;
            }

            if (currentAggregate != null && currentAggregate.equals(newValue))
            {
                return currentAggregate;
            }

            return null;
        }

        @Override
        public Object applyToDoubleColumn(DfDoubleColumn doubleColumn)
        {
            double first = doubleColumn.getDouble(0);
            if (doubleColumn.toDoubleList().allSatisfy(each -> each == first))
            {
                return first;
            }

            return null;
        }

        @Override
        public Object applyToLongColumn(DfLongColumn longColumn)
        {
            long first = longColumn.getLong(0);
            if (longColumn.toLongList().allSatisfy(each -> each == first))
            {
                return first;
            }

            return null;
        }

        @Override
        public Object applyIterable(ListIterable<?> items)
        {
            Object first = items.getFirst();
            if (items.allSatisfy(each -> each.equals(first)))
            {
                return first;
            }

            return null;
        }

        @Override
        public String getDescription()
        {
            return "All values in the set are the same";
        }

        @Override
        public ValueType targetColumnType(ValueType sourceColumnType)
        {
            return sourceColumnType;
        }
    }
}

