package io.github.vmzakharov.ecdataframe.dataframe;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.github.vmzakharov.ecdataframe.dataframe.DfColumnSortOrder.DESC;
import static org.junit.jupiter.api.Assertions.*;

public class DataFrameIterateTest
{
    private DataFrame dataFrame;

    @BeforeEach
    public void setUpDataFrame()
    {
        this.dataFrame = new DataFrame("FrameOfData")
            .addStringColumn("Name").addLongColumn("Bar").addDoubleColumn("Baz")
            .addDateColumn("Date").addDateTimeColumn("DateTime").addDecimalColumn("Decimal")
            .addIntColumn("Qux").addFloatColumn("Thud")
            .addRow("Alice",   11L, 20.0, LocalDate.of(2023, 10, 21), LocalDateTime.of(2023, 10, 21, 11, 11, 11), BigDecimal.valueOf(123, 2), 110, 10.25f)
            .addRow("Carol",   12L, 10.0, LocalDate.of(2023, 12, 24), LocalDateTime.of(2023, 10, 21, 11, 11, 11), BigDecimal.valueOf(124, 2), 120, 12.50f)
            .addRow("Alice",   33L, 25.0, LocalDate.of(2023, 11, 23), LocalDateTime.of(2023, 11, 23, 11, 13, 11), BigDecimal.valueOf(123, 2), 330, 14.75f)
            .addRow("Carol",   24L, 66.1, LocalDate.of(2023, 12, 24), LocalDateTime.of(2023, 12, 24, 11, 14, 11), BigDecimal.valueOf(222, 2), 240, 16.25f)
            .addRow("Carol",   24L, 41.0, LocalDate.of(2023, 12, 24), LocalDateTime.of(2023, 12, 24, 11, 15, 11), BigDecimal.valueOf(333, 2), 240, 18.50f)
            .addRow("Abigail", 33L, 11.0, LocalDate.of(2023, 11, 23), LocalDateTime.of(2023, 10, 21, 11, 11, 11), BigDecimal.valueOf(123, 2), 330, 20.75f)
        ;
    }

    @Test
    public void dataFrameForEach()
    {
        StringBuilder nameBarBaz = new StringBuilder();

        this.dataFrame.forEach(c ->
            nameBarBaz.append(c.getString("Name")).append(',')
                      .append(c.getLong("Bar")).append(',')
                      .append(c.getDouble("Baz")).append(',')
                      .append(c.getInt("Qux")).append(',')
                      .append(c.getFloat("Thud")).append('\n')
        );

        assertEquals(
                """
                Alice,11,20.0,110,10.25
                Carol,12,10.0,120,12.5
                Alice,33,25.0,330,14.75
                Carol,24,66.1,240,16.25
                Carol,24,41.0,240,18.5
                Abigail,33,11.0,330,20.75
                """
            ,
            nameBarBaz.toString()
        );

        MutableBag<LocalDate> dateCount = Bags.mutable.of();

        this.dataFrame.forEach(c -> dateCount.add(c.getDate("Date")));

        assertEquals(1, dateCount.occurrencesOf(LocalDate.of(2023, 10, 21)));
        assertEquals(3, dateCount.occurrencesOf(LocalDate.of(2023, 12, 24)));
        assertEquals(2, dateCount.occurrencesOf(LocalDate.of(2023, 11, 23)));

        MutableBag<LocalDateTime> dateTimeCount = Bags.mutable.of();

        this.dataFrame.forEach(c -> dateTimeCount.add(c.getDateTime("DateTime")));

        assertEquals(3, dateTimeCount.occurrencesOf(LocalDateTime.of(2023, 10, 21, 11, 11, 11)));
        assertEquals(1, dateTimeCount.occurrencesOf(LocalDateTime.of(2023, 11, 23, 11, 13, 11)));
        assertEquals(1, dateTimeCount.occurrencesOf(LocalDateTime.of(2023, 12, 24, 11, 14, 11)));
        assertEquals(1, dateTimeCount.occurrencesOf(LocalDateTime.of(2023, 12, 24, 11, 15, 11)));

        MutableBag<BigDecimal> decimalCount = Bags.mutable.of();

        this.dataFrame.forEach(c -> decimalCount.add(c.getDecimal("Decimal")));

        assertEquals(3, decimalCount.occurrencesOf(BigDecimal.valueOf(123, 2)));
        assertEquals(1, decimalCount.occurrencesOf(BigDecimal.valueOf(124, 2)));
        assertEquals(1, decimalCount.occurrencesOf(BigDecimal.valueOf(222, 2)));
        assertEquals(1, decimalCount.occurrencesOf(BigDecimal.valueOf(333, 2)));
    }

    @Test
    public void sortedDataFrameForEach()
    {
        StringBuilder nameBarBaz = new StringBuilder();

        this.dataFrame.sortBy(Lists.immutable.of("Name", "Bar"), Lists.immutable.of(DfColumnSortOrder.ASC, DESC));

        this.dataFrame.forEach(c ->
            nameBarBaz.append(c.getString("Name")).append(',')
                      .append(c.getLong("Bar")).append(',')
                      .append(c.getDouble("Baz")).append(',')
                      .append(c.getInt("Qux")).append('\n')
        );

       assertEquals(
               """
               Abigail,33,11.0,330
               Alice,33,25.0,330
               Alice,11,20.0,110
               Carol,24,66.1,240
               Carol,24,41.0,240
               Carol,12,10.0,120
               """
            ,
            nameBarBaz.toString()
        );
    }

    @Test
    public void emptyDataFrameForEach()
    {
        this.dataFrame.cloneStructure("Empty").forEach(c -> fail());
    }

    @Test
    public void indexIterate()
    {
        this.dataFrame.createIndex("anIndex", Lists.immutable.of("Name", "Bar"));

        assertEquals(2, this.dataFrame.index("anIndex").sizeAt("Carol", 24L));

        ListIterable<BigDecimal> expectedDecimal = Lists.immutable.of(BigDecimal.valueOf(222, 2), BigDecimal.valueOf(333, 2));

        this.dataFrame.index("anIndex")
                 .iterateAt("Carol", 24L)
                 .forEach(c -> assertTrue(expectedDecimal.contains(c.getDecimal("Decimal"))));

        DoubleList expectedDouble = DoubleLists.immutable.of(66.1, 41.0);

        this.dataFrame.index("anIndex")
                 .iterateAt("Carol", 24L)
                 .forEach(c -> assertTrue(expectedDouble.contains(c.getDouble("Baz"))));

        FloatList expectedFloat = FloatLists.immutable.of(16.25f, 18.5f);

        this.dataFrame.index("anIndex")
                 .iterateAt("Carol", 24L)
                 .forEach(c -> assertTrue(expectedFloat.contains(c.getFloat("Thud"))));

        ListIterable<LocalDate> expectedDates = Lists.immutable.of(LocalDate.of(2023, 12, 24));

        this.dataFrame.index("anIndex")
                 .iterateAt("Carol", 24L)
                 .forEach(c -> assertTrue(expectedDates.contains(c.getDate("Date"))));

        ListIterable<LocalDateTime> expectedDateTimes =
                Lists.immutable.of(LocalDateTime.of(2023, 12, 24, 11, 14, 11), LocalDateTime.of(2023, 12, 24, 11, 15, 11));

        this.dataFrame.index("anIndex")
                 .iterateAt("Carol", 24L)
                 .forEach(
                         c -> assertTrue(expectedDateTimes.contains(c.getDateTime("DateTime")))
                 );

        this.dataFrame.index("anIndex")
                 .iterateAt("Dave", 48L)
                 .forEach(c -> fail());
    }

    @Test
    public void iterateWithGetObject()
    {
        StringBuilder sb = new StringBuilder();
        this.dataFrame.forEach(c -> sb.append(c.getObject("Name")).append('|').append(c.getObject("Bar")).append('\n'));
        assertEquals(
                """
                Alice|11
                Carol|12
                Alice|33
                Carol|24
                Carol|24
                Abigail|33
                """, sb.toString());
    }
}
