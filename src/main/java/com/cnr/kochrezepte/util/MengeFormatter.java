package com.cnr.kochrezepte.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class MengeFormatter {

    private static final DecimalFormat FORMAT =
            new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.GERMANY));

    private MengeFormatter() {
    }

    public static String format(double menge) {
        return FORMAT.format(menge);
    }
}