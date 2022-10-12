package io.github.vmzakharov.ecdataframe.dataframe;

import io.github.vmzakharov.ecdataframe.dataframe.compare.StringComparisonResult;
import io.github.vmzakharov.ecdataframe.dsl.value.StringValue;
import io.github.vmzakharov.ecdataframe.dsl.value.Value;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;

public interface DfStringColumn
extends DfObjectColumn<String>
{
    @Override
    default String getValueAsString(int rowIndex)
    {
        return this.getTypedObject(rowIndex);
    }

    @Override
    default String getValueAsStringLiteral(int rowIndex)
    {
        String value = this.getTypedObject(rowIndex);
        return value == null ? "" : '"' + this.getValueAsString(rowIndex) + '"';
    }

    default ValueType getType()
    {
        return ValueType.STRING;
    }

    @Override
    default Value objectToValue(String anObject)
    {
        return new StringValue(anObject);
    }

    @Override
    default void addRowToColumn(int rowIndex, DfColumn target)
    {
        target.addObject(this.getTypedObject(rowIndex));
    }

    @Override
    default DfCellComparator columnComparator(DfColumn otherColumn)
    {
        DfStringColumn otherStringColumn = (DfStringColumn) otherColumn;

        return (thisRowIndex, otherRowIndex) -> new StringComparisonResult(
                this.getTypedObject(this.dataFrameRowIndex(thisRowIndex)),
                otherStringColumn.getTypedObject(otherStringColumn.dataFrameRowIndex(otherRowIndex)));
    }
}
