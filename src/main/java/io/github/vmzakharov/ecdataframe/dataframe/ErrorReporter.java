package io.github.vmzakharov.ecdataframe.dataframe;

import io.github.vmzakharov.ecdataframe.util.PrinterFactory;

public class ErrorReporter
{
    public static void reportAndThrow(boolean badThingHappened, String errorText)
    {
        if (badThingHappened)
        {
            ErrorReporter.reportAndThrow(errorText);
        }
    }

    public static void reportAndThrow(String errorText)
    {
        PrinterFactory.getPrinter().println("ERROR: " + errorText);
        throw new RuntimeException(errorText);
    }

    public static void reportAndThrow(String errorText, Throwable t)
    {
        PrinterFactory.getPrinter().println("ERROR: " + errorText);
        throw new RuntimeException(errorText, t);
    }
}