package io.github.vmzakharov.ecdataframe.dataframe;

import org.eclipse.collections.impl.factory.Lists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DataFrameDistinctTest
{
    private DataFrame df;

    @BeforeEach
    public void setupDataFrame()
    {
        this.df = new DataFrame("Data Frame")
            .addLongColumn("Id").addStringColumn("Name").addDoubleColumn("Foo").addDateColumn("Bar").addDateTimeColumn("Baz").addDecimalColumn("Qux").addIntColumn("Fred")
            .addRow(3, "Carl", 789.001, LocalDate.of(2022, 11, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(456, 2), 3)
            .addRow(1, "Alice", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(123, 2), 1)
            .addRow(2, "Bob", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(123, 2), 2)
            .addRow(1, "Alice", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(123, 2), 1)
            .addRow(1, "Alice", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(123, 2), 1)
            .addRow(2, "Bob", 123.456, LocalDate.of(2022, 11, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(456, 2), 2)
            .addRow(3, "Carl", 789.001, LocalDate.of(2022, 11, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(456, 2), 3)
            .addRow(1, "Alice", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(567, 1), 1)
            .addRow(3, "Carl", 789.001, LocalDate.of(2022, 11, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(456, 2), 3)
            .addRow(1, "Alice", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(123, 2), 1)
            ;
    }

    @Test
    public void allColumns()
    {
        DataFrameUtil.assertEquals(
            new DataFrame("expected")
                .addLongColumn("Id").addStringColumn("Name").addDoubleColumn("Foo").addDateColumn("Bar").addDateTimeColumn("Baz").addDecimalColumn("Qux").addIntColumn("Fred")
                .addRow(3, "Carl", 789.001, LocalDate.of(2022, 11, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(456, 2), 3)
                .addRow(1, "Alice", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(123, 2), 1)
                .addRow(2, "Bob", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(123, 2), 2)
                .addRow(2, "Bob", 123.456, LocalDate.of(2022, 11, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(456, 2), 2)
                .addRow(1, "Alice", 123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(567, 1), 1)
            ,
            this.df.distinct()
        );
    }

    @Test
    public void oneColumn()
    {
        DataFrameUtil.assertEquals(
            new DataFrame("expected")
                .addStringColumn("Name")
                .addRow("Carl")
                .addRow("Alice")
                .addRow("Bob")
            ,
            this.df.distinct(Lists.immutable.of("Name"))
        );
    }

    @Test
    public void someColumns()
    {
        DataFrameUtil.assertEquals(
            new DataFrame("expected")
                .addLongColumn("Id").addStringColumn("Name").addDoubleColumn("Foo").addIntColumn("Fred")
                .addRow(3, "Carl", 789.001, 3)
                .addRow(1, "Alice", 123.456, 1)
                .addRow(2, "Bob", 123.456, 2)
            ,
            this.df.distinct(Lists.immutable.of("Id", "Name", "Foo", "Fred"))
        );
    }

    @Test
    public void someOtherColumns()
    {
        DataFrameUtil.assertEquals(
            new DataFrame("expected")
                .addDoubleColumn("Foo").addDateColumn("Bar").addDateTimeColumn("Baz").addDecimalColumn("Qux")
                .addRow(789.001, LocalDate.of(2022, 11, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(456, 2))
                .addRow(123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(123, 2))
                .addRow(123.456, LocalDate.of(2022, 11, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(456, 2))
                .addRow(123.456, LocalDate.of(2022, 12, 15), LocalDateTime.of(2023, 11, 22, 20, 45, 55), BigDecimal.valueOf(567, 1))
            ,
            this.df.distinct(Lists.immutable.of("Foo", "Bar", "Baz", "Qux"))
        );
    }
}
