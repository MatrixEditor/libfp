package io.github.libfp.benchmark;//@date 01.11.2023

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.TestOnly;

/**
 * A record representing test accuracy metrics based on true positives (TP),
 * false positives (FP), true negatives (TN), and false negatives (FN). It
 * provides various methods to calculate commonly used evaluation metrics for
 * binary classification tests.
 */
@ApiStatus.Experimental
@TestOnly
public record TestAccuracy(
        int TP, // true positives
        int FP, // false positives
        int TN, // true negatives
        int FN  // false negatives
)
{
    /**
     * Calculates the recall, sensitivity, hit rate, or true positive rate
     * (TPR).
     *
     * @return The calculated recall as a double value.
     */
    public double recall()
    {
        return (1.0 * TP) / (TP + FN);
    }

    /**
     * Calculates the specificity, selectivity, or true negative rate (TNR).
     *
     * @return The calculated specificity as a double value.
     */
    public double specificity()
    {
        return (1.0 * TN) / (TN + FP);
    }

    /**
     * Calculates the precision or positive predictive value (PPV).
     *
     * @return The calculated precision as a double value.
     */
    public double precision()
    {
        return (1.0 * TP) / (TP + FP);
    }

    /**
     * Calculates the negative predictive value (NPV).
     *
     * @return The calculated NPV as a double value.
     */
    public double NPV()
    {
        return (1.0 * TN) / (TN + FN);
    }

    /**
     * Calculates the miss rate or false negative rate (FNR).
     *
     * @return The calculated miss rate as a double value.
     */
    public double missRate()
    {
        return (1.0 * FN) / (FN + TP);
    }

    /**
     * Calculates the fall-out or false positive rate (FPR).
     *
     * @return The calculated fall-out as a double value.
     */
    public double fallOut()
    {
        return (1.0 * FP) / (FP + TN);
    }

    /**
     * Calculates the false discovery rate (FDR).
     *
     * @return The calculated FDR as a double value.
     */
    public double FDR()
    {
        return (1.0 * FP) / (FP + TP);
    }

    /**
     * Calculates the false omission rate (FOR).
     *
     * @return The calculated FOR as a double value.
     */
    public double FOR()
    {
        return (1.0 * FN) / (FN + TN);
    }

    /**
     * Calculates the accuracy (ACC).
     *
     * @return The calculated accuracy as a double value.
     */
    public double accuracy()
    {
        return (1.0 * TP + TN) / (TP + TN + FP + FN);
    }

    /**
     * Calculates the F1 score, which is the harmonic mean of precision and
     * sensitivity.
     *
     * @return The calculated F1 score as a double value.
     */
    public double f1score()
    {
        return 2.0 * ((recall() * precision()) / (recall() + precision()));
    }
}
