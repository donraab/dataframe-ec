package io.github.vmzakharov.ecdataframe.dsl;

import io.github.vmzakharov.ecdataframe.dataset.HierarchicalDataSet;
import io.github.vmzakharov.ecdataframe.dsl.value.Value;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;

import java.time.LocalDate;

public interface EvalContext
{
    Value setVariable(String newVarName, Value newValue);

    Value getVariable(String newVariableName);

    Value getVariableOrDefault(String newVariableName, Value defaultValue);

    boolean hasVariable(String variableName);

    void removeVariable(String variableName);

    MapIterable<String, FunctionScript> getDeclaredFunctions();

    void setDeclaredFunctions(MutableMap<String, FunctionScript> newDeclaredFunctions);

    FunctionScript getDeclaredFunction(String functionName);

    void addDataSet(HierarchicalDataSet dataSet);

    HierarchicalDataSet getDataSet(String dataSetName);

    RichIterable<String> getVariableNames();

    void removeAllVariables();

    String getString(String variableName);

    long getLong(String variableName);

    LocalDate getDate(String variableName);
}
