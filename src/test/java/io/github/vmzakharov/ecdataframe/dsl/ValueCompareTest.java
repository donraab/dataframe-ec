package io.github.vmzakharov.ecdataframe.dsl;

import io.github.vmzakharov.ecdataframe.dsl.value.DateTimeValue;
import io.github.vmzakharov.ecdataframe.dsl.value.DateValue;
import io.github.vmzakharov.ecdataframe.dsl.value.DecimalValue;
import io.github.vmzakharov.ecdataframe.dsl.value.DoubleValue;
import io.github.vmzakharov.ecdataframe.dsl.value.FloatValue;
import io.github.vmzakharov.ecdataframe.dsl.value.IntValue;
import io.github.vmzakharov.ecdataframe.dsl.value.LongValue;
import io.github.vmzakharov.ecdataframe.dsl.value.StringValue;
import io.github.vmzakharov.ecdataframe.dsl.value.Value;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ValueCompareTest
{
    @Test
    public void longValueCompare()
    {
        LongValue lv1 = new LongValue(1);
        LongValue lv2 = new LongValue(2);
        LongValue lv1more = new LongValue(1);

        assertEquals(0, lv1.compareTo(lv1more));
        assertEquals(0, lv2.compareTo(lv2));
        assertTrue(lv1.compareTo(lv2) < 0);
        assertTrue(lv2.compareTo(lv1) > 0);

        assertTrue(lv1.compareTo(Value.VOID) > 0);
        assertTrue(Value.VOID.compareTo(lv1) < 0);
    }

    @Test
    public void doubleValueCompare()
    {
        DoubleValue dv1 = new DoubleValue(1.0);
        DoubleValue dv2 = new DoubleValue(2.0);
        DoubleValue dv1more = new DoubleValue(1.0);

        assertEquals(0, dv1.compareTo(dv1more));
        assertEquals(0, dv2.compareTo(dv2));
        assertTrue(dv1.compareTo(dv2) < 0);
        assertTrue(dv2.compareTo(dv1) > 0);

        assertTrue(dv1.compareTo(Value.VOID) > 0);
        assertTrue(Value.VOID.compareTo(dv1) < 0);
    }

    @Test
    public void intValueCompare()
    {
        IntValue iv1 = new IntValue(1);
        IntValue iv2 = new IntValue(2);
        IntValue iv1more = new IntValue(1);

        assertEquals(0, iv1.compareTo(iv1more));
        assertEquals(0, iv2.compareTo(iv2));
        assertTrue(iv1.compareTo(iv2) < 0);
        assertTrue(iv2.compareTo(iv1) > 0);

        assertTrue(iv1.compareTo(Value.VOID) > 0);
        assertTrue(Value.VOID.compareTo(iv1) < 0);
    }

    @Test
    public void floatValueCompare()
    {
        FloatValue iv1 = new FloatValue(1.0f);
        FloatValue iv2 = new FloatValue(2.0f);
        FloatValue iv1more = new FloatValue(1.0f);

        assertEquals(0, iv1.compareTo(iv1more));
        assertEquals(0, iv2.compareTo(iv2));
        assertTrue(iv1.compareTo(iv2) < 0);
        assertTrue(iv2.compareTo(iv1) > 0);

        assertTrue(iv1.compareTo(Value.VOID) > 0);
        assertTrue(Value.VOID.compareTo(iv1) < 0);
    }

    @Test
    public void stringValueCompare()
    {
        StringValue abc = new StringValue("abc");
        StringValue def = new StringValue("def");
        StringValue abcMore = new StringValue("abc");

        assertEquals(0, abc.compareTo(abcMore));
        assertEquals(0, def.compareTo(def));
        assertTrue(abc.compareTo(def) < 0);
        assertTrue(def.compareTo(abc) > 0);

        assertTrue(abc.compareTo(Value.VOID) > 0);
        assertTrue(Value.VOID.compareTo(abc) < 0);
    }

    @Test
    public void dateValueCompare()
    {
        DateValue abc = new DateValue(LocalDate.of(2024, 3, 22));
        DateValue def = new DateValue(LocalDate.of(2025, 12, 23));
        DateValue abcMore = new DateValue(LocalDate.of(2024, 3, 22));

        assertEquals(0, abc.compareTo(abcMore));
        assertEquals(0, def.compareTo(def));
        assertTrue(abc.compareTo(def) < 0);
        assertTrue(def.compareTo(abc) > 0);

        assertTrue(abc.compareTo(Value.VOID) > 0);
        assertTrue(Value.VOID.compareTo(abc) < 0);
    }

    @Test
    public void dateTimeValueCompare()
    {
        DateTimeValue abc = new DateTimeValue(LocalDateTime.of(2024, 3, 22, 14, 25, 46));
        DateTimeValue def = new DateTimeValue(LocalDateTime.of(2025, 12, 25, 23, 10, 12));
        DateTimeValue abcMore = new DateTimeValue(LocalDateTime.of(2024, 3, 22, 14, 25, 46));

        assertEquals(0, abc.compareTo(abcMore));
        assertEquals(0, def.compareTo(def));
        assertTrue(abc.compareTo(def) < 0);
        assertTrue(def.compareTo(abc) > 0);

        assertTrue(abc.compareTo(Value.VOID) > 0);
        assertTrue(Value.VOID.compareTo(abc) < 0);
    }

    @Test
    public void decimalValueCompare()
    {
        DecimalValue abc = new DecimalValue(BigDecimal.valueOf(1234, 2));
        DecimalValue def = new DecimalValue(BigDecimal.valueOf(4567, 1));
        DecimalValue abcMore = new DecimalValue(BigDecimal.valueOf(1234, 2));

        assertEquals(0, abc.compareTo(abcMore));
        assertEquals(0, def.compareTo(def));
        assertTrue(abc.compareTo(def) < 0);
        assertTrue(def.compareTo(abc) > 0);

        assertTrue(abc.compareTo(Value.VOID) > 0);
        assertTrue(Value.VOID.compareTo(abc) < 0);
    }

    @Test
    public void compareVoids()
    {
        assertEquals(0, Value.VOID.compareTo(Value.VOID));
    }

    @Test
    public void compareToNull()
    {
        assertThrows(RuntimeException.class, () -> new StringValue("abc").compareTo(null));
    }

    @Test
    public void compareDifferentTypes()
    {
        assertThrows(RuntimeException.class, () -> new StringValue("abc").compareTo(new LongValue(123)));
    }
}
