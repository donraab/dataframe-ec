package io.github.vmzakharov.ecdataframe.dsl.value;

  import io.github.vmzakharov.ecdataframe.dsl.ArithmeticOp;
import io.github.vmzakharov.ecdataframe.dsl.Expression;
import io.github.vmzakharov.ecdataframe.dsl.PredicateOp;
import io.github.vmzakharov.ecdataframe.dsl.UnaryOp;
import io.github.vmzakharov.ecdataframe.dsl.visitor.ExpressionEvaluationVisitor;
import io.github.vmzakharov.ecdataframe.dsl.visitor.ExpressionVisitor;

import static io.github.vmzakharov.ecdataframe.util.ExceptionFactory.exceptionByKey;

public interface Value
extends Expression, Comparable<Value>
{
    Value VOID = new VoidValue();

    default String stringValue()
    {
        return this.asStringLiteral();
    }

    String asStringLiteral();

    default Value apply(Value another, ArithmeticOp operation)
    {
        throw exceptionByKey("DSL_UNDEFINED_OP_ON_VALUE")
            .with("operation", operation.asString()).with("value", this.asStringLiteral())
            .getUnsupported();
    }

    default Value apply(UnaryOp operation)
    {
        throw exceptionByKey("DSL_UNDEFINED_OP_ON_VALUE")
            .with("operation", operation.asString()).with("value", this.asStringLiteral())
            .getUnsupported();
    }

    default BooleanValue applyPredicate(Value another, PredicateOp operation)
    {
        throw exceptionByKey("DSL_UNDEFINED_OP_ON_VALUE")
            .with("operation", operation.asString()).with("value", this.asStringLiteral())
            .getUnsupported();
    }

    @Override
    default Value evaluate(ExpressionEvaluationVisitor visitor)
    {
        return visitor.visitConstExpr(this);
    }

    @Override
    default void accept(ExpressionVisitor visitor)
    {
        visitor.visitConstExpr(this);
    }

    ValueType getType();

    @Override
    default int compareTo(Value o)
    {
        throw exceptionByKey("DSL_COMPARE_NOT_SUPPORTED").with("type", this.getType()).getUnsupported();
    }

    default void throwExceptionIfNull(Object newValue)
    {
        if (newValue == null)
        {
            exceptionByKey("DSL_NULL_VALUE_NOT_ALLOWED").with("type", this.getType()).fire();
        }
    }

    default void checkSameTypeForComparison(Value other)
    {
        if (null == other)
        {
            throw exceptionByKey("DSL_COMPARE_TO_NULL")
                    .with("className",  this.getClass().getSimpleName()).getUnsupported();
        }

        if (!other.isVoid() && (this.getType() != other.getType()))
        {
            throw exceptionByKey("DSL_COMPARE_INCOMPATIBLE")
                    .with("className",  this.getClass().getSimpleName())
                    .with("otherClassName", other.getClass().getSimpleName())
                    .getUnsupported();
        }
    }

    default boolean isVoid()
    {
        return false;
    }

    default boolean isBoolean()
    {
        return this.getType().isBoolean();
    }

    default boolean isLong()
    {
        return this.getType().isLong();
    }

    default boolean isInt()
    {
        return this.getType().isInt();
    }

    default boolean isDouble()
    {
        return this.getType().isDouble();
    }

    default boolean isFloat()
    {
        return this.getType().isFloat();
    }

    default boolean isDecimal()
    {
        return this.getType().isDecimal();
    }

    default boolean isNumber()
    {
        return this.getType().isNumber();
    }

    default boolean isWholeNumber()
    {
        return this.getType().isWholeNumber();
    }

    default boolean isRealNumber()
    {
        return this.getType().isRealNumber();
    }

    default boolean isString()
    {
        return this.getType().isString();
    }

    default boolean isDate()
    {
        return this.getType().isDate();
    }

    default boolean isTemporal()
    {
        return this.getType().isTemporal();
    }

    default boolean isDateTime()
    {
        return this.getType().isDateTime();
    }

    default boolean isVector()
    {
        return this.getType().isVector();
    }

    default boolean isDataFrame()
    {
        return this.getType().isString();
    }

    final class VoidValue implements Value
    {
        private VoidValue()
        {
        }

        @Override
        public String asStringLiteral()
        {
            return "VOID";
        }

        @Override
        public ValueType getType()
        {
            return ValueType.VOID;
        }

        @Override
        public boolean isVoid()
        {
            return true;
        }

        @Override
        public int compareTo(Value other)
        {
            return this == other ? 0 : -1;
        }
    }
}
