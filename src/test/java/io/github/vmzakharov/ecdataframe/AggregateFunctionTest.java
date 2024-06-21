package io.github.vmzakharov.ecdataframe;

import io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import io.github.vmzakharov.ecdataframe.util.FormatWithPlaceholders;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AggregateFunctionTest
{
    @Test
    public void testClone()
    {
        AggregateFunction sum = AggregateFunction.sum("Foo", "Bar");
        AggregateFunction cloned = sum.cloneWith("Baz", "Qux");

        assertEquals("Baz", cloned.getSourceColumnName());
        assertEquals("Qux", cloned.getTargetColumnName());
        assertEquals(sum.getClass(), cloned.getClass());
    }

    @Test
    public void testFailureToClone()
    {
        AggregateFunction wontClone = new CloneResistant();

        Exception exception = assertThrows(
                RuntimeException.class,
                () ->  wontClone.cloneWith("Baz", "Qux")
        );

        assertEquals(
                FormatWithPlaceholders
                    .messageFromKey("AGG_CANNOT_CLONE")
                    .with("operation", wontClone.getName())
                    .toString(),
                exception.getMessage()
        );
    }

    private static class CloneResistant
    extends AggregateFunction
    {
        public CloneResistant()
        {
            super("ABC", "XYZ");
        }

        @Override
        public ListIterable<ValueType> supportedSourceTypes()
        {
            return Lists.immutable.of(ValueType.STRING);
        }
    }
}
