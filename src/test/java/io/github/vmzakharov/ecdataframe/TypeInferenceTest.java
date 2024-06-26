package io.github.vmzakharov.ecdataframe;

import io.github.vmzakharov.ecdataframe.dsl.EvalContext;
import io.github.vmzakharov.ecdataframe.dsl.Script;
import io.github.vmzakharov.ecdataframe.dsl.SimpleEvalContext;
import io.github.vmzakharov.ecdataframe.dsl.value.FloatValue;
import io.github.vmzakharov.ecdataframe.dsl.value.IntValue;
import io.github.vmzakharov.ecdataframe.dsl.value.LongValue;
import io.github.vmzakharov.ecdataframe.dsl.value.StringValue;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import io.github.vmzakharov.ecdataframe.dsl.visitor.PrettyPrintVisitor;
import io.github.vmzakharov.ecdataframe.dsl.visitor.TypeInferenceVisitor;
import io.github.vmzakharov.ecdataframe.util.ConfigureMessages;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.github.vmzakharov.ecdataframe.TypeInferenceUtil.assertScriptType;
import static io.github.vmzakharov.ecdataframe.util.FormatWithPlaceholders.messageFromKey;
import static org.junit.jupiter.api.Assertions.*;

public class TypeInferenceTest
{
    @BeforeAll
    public static void initializeErrorMessages()
    {
        ConfigureMessages.initialize();
    }

    @Test
    public void longTypeInference()
    {
        assertScriptType("(1 + 2) * 3", ValueType.LONG);
        assertScriptType("x = (1 + 2) * 3", ValueType.LONG);
        assertScriptType("-123", ValueType.LONG);
    }

    @Test
    public void doubleTypeInference()
    {
        assertScriptType("(1.0 + 2.0) * 3.0", ValueType.DOUBLE);
        assertScriptType("x = 1.0 + 2.0", ValueType.DOUBLE);
        assertScriptType("x = (1 + 2) * 3.0", ValueType.DOUBLE);
        assertScriptType("-4.56", ValueType.DOUBLE);
    }

    @Test
    public void stringTypeInference()
    {
        assertScriptType("\"abc\"", ValueType.STRING);
        assertScriptType("x = \"abc\" + \"def\"", ValueType.STRING);
    }

    @Test
    public void booleanTypeInference()
    {
        assertScriptType("'abc' == 'xyz'", ValueType.BOOLEAN);
        assertScriptType("1 in (1, 2, 3)", ValueType.BOOLEAN);
        assertScriptType("(1, 2, 3) is empty", ValueType.BOOLEAN);
        assertScriptType("'' is not empty", ValueType.BOOLEAN);
        assertScriptType("not ((5 > 6) or (1 in (1, 2, 3)))", ValueType.BOOLEAN);
    }

    @Test
    public void isNullInference()
    {
        assertScriptType("(1, 2, 3) is null", ValueType.BOOLEAN);
        assertScriptType("'' is not null", ValueType.BOOLEAN);
        assertScriptType("123 is null", ValueType.BOOLEAN);
        assertScriptType("123 is not null", ValueType.BOOLEAN);
        assertScriptType("1.234 is null", ValueType.BOOLEAN);
        assertScriptType("toDate(2020, 12, 20) is null", ValueType.BOOLEAN);
        assertScriptType("toDate(2020, 12, 20) is not null", ValueType.BOOLEAN);
        assertScriptType("""
                a = 1
                if a is null then
                  'null'
                else
                  'not null'
                endif""", ValueType.STRING);
    }

    @Test
    public void projectionInference()
    {
        assertScriptType(
                """
                project {
                  p.x,
                  p.y,
                  abc: 'abc'
                }""", ValueType.DATA_FRAME);
    }

    @Test
    public void scriptTypeInference()
    {
        assertScriptType("""
                1
                2.0
                "abc\"""", ValueType.STRING);

        assertScriptType("""
                a = 1
                b = 2.0
                c = a + b""", ValueType.DOUBLE);
    }

    @Test
    public void functionTypeInference()
    {
        assertScriptType("""
                function sum(a, b)
                {
                    a + b
                }
                x = 1.0
                y = 2.0
                sum(x, y)""", ValueType.DOUBLE);

        assertScriptType("""
                function sum(a, b)
                {
                    a + b
                }
                x = 1
                y = 2
                sum(x, y)""", ValueType.LONG);

        assertScriptType("""
                function sum(a, b)
                {
                    a + b
                }
                x = "Hello, "
                y = "world!"
                sum(x, y)""", ValueType.STRING);

        assertScriptType("""
                function sum(a, b)
                {
                    a + b
                }
                x = 1
                y = 2.0
                sum(x, y)""", ValueType.DOUBLE);

        assertScriptType("""
                function isItBigger(a, b) { a > b }
                x = 1
                y = 2.0
                isItBigger(x, y)""", ValueType.BOOLEAN);
    }

    @Test
    public void conditionalStatement()
    {
        assertScriptType("a > b ? 5 : 7", ValueType.LONG);
        assertScriptType("a > b ? 5.0 : 7", ValueType.DOUBLE);
        assertScriptType("a > b ? 'foo' : 'bar'", ValueType.STRING);
        assertScriptType("a > b ? 'foo' : 7", ValueType.VOID);
    }

    @Test
    public void conditionalOnlyIfBranch()
    {
        assertScriptType("if a > b then\n  5\nendif", ValueType.LONG);
        assertScriptType("if a > b then\n  5.5 + 1\nendif", ValueType.DOUBLE);
        assertScriptType("if a > b then\n  '5'\nendif", ValueType.STRING);
    }

    @Test
    public void longWithContext()
    {
        SimpleEvalContext context = new SimpleEvalContext();
        context.setVariable("a", new LongValue(5));
        context.setVariable("b", new LongValue(7));
        assertScriptType("a + b", context, ValueType.LONG);
    }

    @Test
    public void intWithContext()
    {
        SimpleEvalContext context = new SimpleEvalContext();
        context.setVariable("a", new IntValue(4));
        context.setVariable("b", new IntValue(5));
        assertScriptType("a + b", context, ValueType.LONG);
        assertScriptType("-a", context, ValueType.INT);
    }

    @Test
    public void builtInFunctions()
    {
        assertScriptType("abs(123)", ValueType.LONG);
        assertScriptType("abs(12.34)", ValueType.DOUBLE);
        assertScriptType("startsWith('Hello, there', 'Hello')", ValueType.BOOLEAN);
        assertScriptType("substr(' Hello ', 1, 2)", ValueType.STRING);
        assertScriptType("toDate(2020, 11, 22)", ValueType.DATE);
        assertScriptType("toDateTime(2020, 11, 22, 15, 11)", ValueType.DATE_TIME);
        assertScriptType("toDouble('12.34')", ValueType.DOUBLE);
        assertScriptType("toDecimal('12.34')", ValueType.DECIMAL);
        assertScriptType("toLong('1234')", ValueType.LONG);
        assertScriptType("toString(123)", ValueType.STRING);
        assertScriptType("toUpper('abc')", ValueType.STRING);
        assertScriptType("trim(' Hello ')", ValueType.STRING);
        assertScriptType("withinDays(toDate(2020, 11, 22), toDate(2020, 11, 20), 4)", ValueType.BOOLEAN);
    }

    @Test
    public void builtInFunctionsForInt()
    {
        SimpleEvalContext context = new SimpleEvalContext();
        context.setVariable("foo", new IntValue(5));
        assertScriptType("abs(foo)", context, ValueType.INT);
        assertScriptType("toString(foo)", context, ValueType.STRING);
    }

    @Test
    public void builtInFunctionsForFloat()
    {
        SimpleEvalContext context = new SimpleEvalContext();
        context.setVariable("waldo", new FloatValue(5));
        assertScriptType("abs(waldo)", context, ValueType.FLOAT);
        assertScriptType("toString(waldo)", context, ValueType.STRING);
    }

    @Test
    public void decimalInference()
    {
        SimpleEvalContext context = new SimpleEvalContext();
        context.setVariable("foo", new IntValue(5));
        context.setVariable("waldo", new FloatValue(5));

        assertScriptType("toDecimal(123, 2) + 1", ValueType.DECIMAL);
        assertScriptType("toDecimal(123, 2) + 1.0", ValueType.DECIMAL);
        assertScriptType("toDecimal(123, 2) + foo", context, ValueType.DECIMAL);
        assertScriptType("toDecimal(123, 2) + waldo", context, ValueType.DECIMAL);
    }

    @Test
    public void vectors()
    {
        assertScriptType("('a', 'b', 1)", ValueType.VECTOR);
        assertScriptType("v('a', 'b', 1)", ValueType.VECTOR);

        assertScriptType("(1.23, 'b', 1)[0]", ValueType.DOUBLE);
        assertScriptType("(1.23, 'b', 1)[1]", ValueType.STRING);
        assertScriptType("(1.23, 'b', 1)[2]", ValueType.LONG);

        assertScriptType("x = v('a', 'b', 1)\n"
                        + "x[1]",
                ValueType.VOID);

        assertScriptType("""
                        x = v('a', 'b', 1)
                        i = 2
                        x[i]""",
                ValueType.VOID);
    }

    @Test
    public void vectorsWithErrors()
    {
        this.assertError("'abc'[2]",
                messageFromKey("IDX_EXPR_VECTOR_TYPE_INVALID").with("vectorType", ValueType.STRING.name()).toString());

        this.assertErrors("x[45]",
                Lists.immutable.of(
                        Tuples.twin(
                                messageFromKey("TYPE_INFER_UNDEFINED_VARIABLE").toString(),
                                "x"),
                        Tuples.twin(
                                messageFromKey("IDX_EXPR_VECTOR_TYPE_INVALID").with("vectorType", ValueType.VOID.name()).toString(),
                                "x[45]")
                ));

        this.assertError(
                "(1.23, 'b', 1)['abc']",
                messageFromKey("IDX_EXPR_INDEX_TYPE_INVALID").with("indexType", ValueType.STRING.name()).toString());

        this.assertError(
                "(1.23, 'b', 1)[1.23]",
                messageFromKey("IDX_EXPR_INDEX_TYPE_INVALID").with("indexType", ValueType.DOUBLE.name()).toString());
    }

    @Test
    public void containsIncompatibleTypes()
    {
        this.assertError("1 in 'abc'", messageFromKey("TYPE_INFER_TYPES_IN_EXPRESSION").toString());

        this.assertError("1.1 not in 'abc'", messageFromKey("TYPE_INFER_TYPES_IN_EXPRESSION").toString());
    }

    @Test
    public void expressionIncompatibleTypes()
    {
        this.assertError(
                "x = 5\ny = 'abc'\nx + y",
                "(x + y)",
                messageFromKey("TYPE_INFER_TYPES_IN_EXPRESSION").toString());
    }

    @Test
    public void conditionalIncompatibleTypes()
    {
        this.assertError(
                """
                a = 1
                b = 2
                x = 5
                if a > b then
                  x
                else
                  'abc'
                endif""",
                this.prettyPrint("""
                        if a > b then
                          x
                        else
                          'abc'
                        endif"""),
                messageFromKey("TYPE_INFER_ELSE_INCOMPATIBLE").toString());
    }

    @Test
    public void conditionalIncompatibleTypesWithContextVariables()
    {
        SimpleEvalContext context = new SimpleEvalContext();
        context.setVariable("a", new LongValue(5));
        context.setVariable("b", new LongValue(7));
        context.setVariable("y", new StringValue("abc"));
        this.assertError(context,
                "x = 5\nif a > b then\n  x\nelse\n y\nendif",
                this.prettyPrint("if a > b then\n  x\nelse\n y\nendif"),
                messageFromKey("TYPE_INFER_ELSE_INCOMPATIBLE").toString());
    }

    @Test
    public void comparisonIncompatibleTypes()
    {
        this.assertError(
                "x = 5\nx == 'abc'\n",
                this.prettyPrint("x == 'abc'"),
                messageFromKey("TYPE_INFER_TYPES_IN_EXPRESSION").toString());
    }

    /*
     * note that the "cascading" errors are ignored, i.e. we won't generate an error for
     * x + 1 if x is undefined if we have already generated an error for x being undefined
     */
    @Test
    public void manyErrorsInOneScriptCatchesAllErrors()
    {
        this.assertErrors(
                """
                x = 5
                x + 'abc'
                'x' < 1
                """,
                Lists.immutable.of(
                        Tuples.twin(messageFromKey("TYPE_INFER_TYPES_IN_EXPRESSION").toString(), "(x + \"abc\")"),
                        Tuples.twin(messageFromKey("TYPE_INFER_TYPES_IN_EXPRESSION").toString(), "(\"x\" < 1)")
                ));

        this.assertErrors(
                """
                y = 5
                x + 1
                'x' < y
                """,
                Lists.immutable.of(
                        Tuples.twin(messageFromKey("TYPE_INFER_UNDEFINED_VARIABLE").toString(), "x"),
                        Tuples.twin(messageFromKey("TYPE_INFER_TYPES_IN_EXPRESSION").toString(), "(\"x\" < y)")
                ));

        this.assertErrors(
                """
                if x > 3
                then 'abc'
                else 1
                endif""",
                Lists.immutable.of(
                        Tuples.twin(messageFromKey("TYPE_INFER_UNDEFINED_VARIABLE").toString(), "x"),
                        Tuples.twin(messageFromKey("TYPE_INFER_ELSE_INCOMPATIBLE").toString(), """
                                if (x > 3) then
                                  "abc"
                                else
                                  1
                                endif""")
                ));
    }

    @Test
    public void comparisonIncompatibleTypesWithContextVariables()
    {
        SimpleEvalContext context = new SimpleEvalContext();
        context.setVariable("x", new LongValue(1));
        this.assertError(context,
                "x == 'abc'\n",
                this.prettyPrint("x == 'abc'"),
                messageFromKey("TYPE_INFER_TYPES_IN_EXPRESSION").toString());
    }

    @Test
    public void undefinedVariables()
    {
        this.assertError("1 + abc", "abc", messageFromKey("TYPE_INFER_UNDEFINED_VARIABLE").toString());
        this.assertError("x == 'abc'\ny + x", "x", messageFromKey("TYPE_INFER_UNDEFINED_VARIABLE").toString());
        this.assertError(
                """
                if x == 4
                then 'four'
                else 'not four'
                endif""",
                "x", messageFromKey("TYPE_INFER_UNDEFINED_VARIABLE").toString());
    }

    private void assertErrors(String scriptString, ListIterable<Twin<String>> expectedErrors)
    {
        Script script = ExpressionTestUtil.toScript(scriptString);
        TypeInferenceVisitor visitor = new TypeInferenceVisitor();
        script.accept(visitor);

        assertTrue(visitor.hasErrors(), "Expected a type inference error");

        assertEquals(expectedErrors, visitor.getErrors());
    }

    private void assertError(String scriptString, String errorText)
    {
        this.assertError(new SimpleEvalContext(), scriptString, this.prettyPrint(scriptString), errorText);
    }

    private void assertError(String scriptString, String errorDescription, String errorText)
    {
        this.assertError(new SimpleEvalContext(), scriptString, errorDescription, errorText);
    }

    private void assertError(EvalContext context, String scriptString, String errorDescription, String errorText)
    {
        Script script = ExpressionTestUtil.toScript(scriptString);
        TypeInferenceVisitor visitor = new TypeInferenceVisitor(context);
        script.accept(visitor);

        assertTrue(visitor.hasErrors(), "Expected a type inference error");
        assertEquals(errorText, visitor.getErrorDescription(), "Error type");

        assertEquals(errorDescription, visitor.getErrorExpressionString(), "Error expression");
    }

    private String prettyPrint(String expressionString)
    {
        return PrettyPrintVisitor.exprToString(
                ExpressionTestUtil.toScript(expressionString).getExpressions().getFirst());
    }
}
