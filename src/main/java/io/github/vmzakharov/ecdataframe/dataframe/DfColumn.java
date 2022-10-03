package io.github.vmzakharov.ecdataframe.dataframe;

import io.github.vmzakharov.ecdataframe.dsl.value.Value;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;

public interface DfColumn
{
    String getName();

    String getValueAsString(int rowIndex);

    default String getValueAsStringLiteral(int rowIndex)
    {
        return this.isNull(rowIndex) ? "" : this.getValueAsString(rowIndex);
    }

    void addObject(Object newObject);

    void addValue(Value value);

    boolean isNull(int rowIndex);

    Object getObject(int rowIndex);

    DataFrame getDataFrame();

    Value getValue(int rowIndex);

    ValueType getType();

    boolean isStored();

    default boolean isComputed()
    {
        return !this.isStored();
    }

    int getSize();

    void setObject(int rowIndex, Object anObject);

    void addEmptyValue();

    default Object aggregate(AggregateFunction aggregator)
    {
        ErrorReporter.reportAndThrow(
            "Aggregation " + aggregator.getName() + "(" + aggregator.getDescription()
            + ") cannot be performed on column " + this.getName() + " of type " + this.getType());

        return null; // will not get here
    }

    void applyAggregator(int targetRowIndex, DfColumn sourceColumn, int sourceRowIndex, AggregateFunction aggregateFunction);

    DfColumn cloneSchemaAndAttachTo(DataFrame attachTo);

    DfColumn cloneSchemaAndAttachTo(DataFrame attachTo, String newName);

    void addRowToColumn(int rowIndex, DfColumn target);

    default void seal()
    {
        // nothing
    }

    default void enablePooling()
    {
        // nothing
    }

    DfColumn mergeWithInto(DfColumn other, DataFrame target);

    default DfCellComparator columnComparator(DfColumn otherColumn)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    default int dataFrameRowIndex(int virtualRowIndex)
    {
        return this.getDataFrame().rowIndexMap(virtualRowIndex);
    }
}
