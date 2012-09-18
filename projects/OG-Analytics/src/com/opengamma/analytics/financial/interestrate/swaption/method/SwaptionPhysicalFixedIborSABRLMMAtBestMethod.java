/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.swaption.method;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.InterestRateCurveSensitivity;
import com.opengamma.analytics.financial.interestrate.PresentValueSABRSensitivityDataBundle;
import com.opengamma.analytics.financial.interestrate.YieldCurveBundle;
import com.opengamma.analytics.financial.interestrate.method.PricingMethod;
import com.opengamma.analytics.financial.interestrate.swaption.derivative.SwaptionPhysicalFixedIbor;
import com.opengamma.analytics.financial.model.interestrate.definition.LiborMarketModelDisplacedDiffusionDataBundle;
import com.opengamma.analytics.financial.model.interestrate.definition.LiborMarketModelDisplacedDiffusionParameters;
import com.opengamma.analytics.financial.model.option.definition.SABRInterestRateDataBundle;
import com.opengamma.analytics.math.matrix.CommonsMatrixAlgebra;
import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.util.money.CurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;
import com.opengamma.util.tuple.Triple;

/**
 * Method to computes the present value and sensitivities of physical delivery European swaptions with a Libor Market Model calibrated exactly to SABR prices.
 * The LMM displacements and volatility weights are hard coded.
 * <p> Reference: M. Henrard, Algorithmic differentiation and calibration: optimization, September 2012.
 */
public class SwaptionPhysicalFixedIborSABRLMMAtBestMethod implements PricingMethod {

  /**
   * The SABR method used for European swaptions with physical delivery.
   */
  private static final SwaptionPhysicalFixedIborSABRMethod METHOD_SWAPTION_SABR = SwaptionPhysicalFixedIborSABRMethod.getInstance();
  /**
   * The LMM method used for European swaptions with physical delivery.
   */
  private static final SwaptionPhysicalFixedIborLMMDDMethod METHOD_SWAPTION_LMM = SwaptionPhysicalFixedIborLMMDDMethod.getInstance();
  /**
   * The method used to create the calibration basket.
   */
  private static final SwaptionPhysicalFixedIborBasketMethod METHOD_BASKET = SwaptionPhysicalFixedIborBasketMethod.getInstance();
  /**
   * The matrix algebra used.
   */
  private static final CommonsMatrixAlgebra ALGEBRA = new CommonsMatrixAlgebra();
  /**
   * The noneyness of strikes used in the calibration basket. Difference between the swaption rate and the basket rates.
   */
  private final double[] _strikeMoneyness;
  /**
   * The initial value of the LMM parameters for calibration. The initial parameters are not modified by the calibration but a new copy is created for each calibration.
   */
  private final LiborMarketModelDisplacedDiffusionParameters _parametersInit;

  /**
   * Constructor.
   * @param strikeMoneyness The noneyness of strikes used in the calibration basket. Difference between the swaption rate and the basket rates.
   * @param parametersInit The initial value of the LMM parameters for calibration. The initial parameters are not modified by the calibration but a new copy is created for each calibration.
   */
  public SwaptionPhysicalFixedIborSABRLMMAtBestMethod(double[] strikeMoneyness, final LiborMarketModelDisplacedDiffusionParameters parametersInit) {
    _strikeMoneyness = strikeMoneyness;
    _parametersInit = parametersInit;
  }

  /**
   * The method calibrates a LMM on a set of vanilla swaption priced with SABR. The set of vanilla swaptions is given by the CalibrationType.
   * The original swaption is priced with the calibrated LMM. 
   * This should not be used for vanilla swaptions (the price is equal to the SABR price with a longer computation type and some approximation).
   * This is useful for non-standard swaptions like amortized swaptions.
   * @param swaption The swaption.
   * @param curves The curves and SABR data.
   * @return The present value. 
   */
  public CurrencyAmount presentValue(final SwaptionPhysicalFixedIbor swaption, final SABRInterestRateDataBundle curves) {
    Validate.notNull(swaption);
    Validate.notNull(curves);
    int nbStrikes = _strikeMoneyness.length;
    LiborMarketModelDisplacedDiffusionParameters lmmParameters = _parametersInit.copy();
    SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationObjective objective = new SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationObjective(lmmParameters);
    SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationEngine calibrationEngine = new SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationEngine(objective, nbStrikes);
    SwaptionPhysicalFixedIbor[] swaptionCalibration = METHOD_BASKET.calibrationBasketFixedLegPeriod(swaption, _strikeMoneyness);
    calibrationEngine.addInstrument(swaptionCalibration, METHOD_SWAPTION_SABR);
    calibrationEngine.calibrate(curves);
    LiborMarketModelDisplacedDiffusionDataBundle lmmBundle = new LiborMarketModelDisplacedDiffusionDataBundle(lmmParameters, curves);
    CurrencyAmount pv = METHOD_SWAPTION_LMM.presentValue(swaption, lmmBundle);
    return pv;
  }

  @Override
  public CurrencyAmount presentValue(InstrumentDerivative instrument, YieldCurveBundle curves) {
    Validate.isTrue(instrument instanceof SwaptionPhysicalFixedIbor, "Physical delivery swaption");
    Validate.isTrue(curves instanceof LiborMarketModelDisplacedDiffusionDataBundle, "Bundle should contain LMM data");
    return presentValue(instrument, curves);
  }

  public PresentValueSABRSensitivityDataBundle presentValueSABRSensitivity(final SwaptionPhysicalFixedIbor swaption, final SABRInterestRateDataBundle curves) {
    Validate.notNull(swaption);
    Validate.notNull(curves);
    int nbStrikes = _strikeMoneyness.length;
    LiborMarketModelDisplacedDiffusionParameters lmmParameters = _parametersInit.copy();
    SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationObjective objective = new SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationObjective(lmmParameters);
    SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationEngine calibrationEngine = new SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationEngine(objective, nbStrikes);
    SwaptionPhysicalFixedIbor[] swaptionCalibration = METHOD_BASKET.calibrationBasketFixedLegPeriod(swaption, _strikeMoneyness);
    calibrationEngine.addInstrument(swaptionCalibration, METHOD_SWAPTION_SABR);
    calibrationEngine.calibrate(curves);
    LiborMarketModelDisplacedDiffusionDataBundle lmmBundle = new LiborMarketModelDisplacedDiffusionDataBundle(lmmParameters, curves);

    int nbCalibrations = swaptionCalibration.length;
    int nbPeriods = nbCalibrations / nbStrikes;
    int nbFact = lmmParameters.getNbFactor();
    List<Integer> instrumentIndex = calibrationEngine.getInstrumentIndex();
    double[] dPvdPhi = new double[2 * nbPeriods];
    // Implementation note: Derivative of the priced swaptions wrt the calibration parameters (multiplicative factor and additive term)
    double[][] dPvdGamma = METHOD_SWAPTION_LMM.presentValueLMMSensitivity(swaption, lmmBundle);
    double[] dPvdDis = METHOD_SWAPTION_LMM.presentValueDDSensitivity(swaption, lmmBundle);
    for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
      for (int loopsub = instrumentIndex.get(loopperiod * nbStrikes); loopsub < instrumentIndex.get((loopperiod + 1) * nbStrikes); loopsub++) {
        for (int loopfact = 0; loopfact < nbFact; loopfact++) {
          dPvdPhi[loopperiod] += dPvdGamma[loopsub][loopfact] * lmmParameters.getVolatility()[loopsub][loopfact];
          dPvdPhi[nbPeriods + loopperiod] += dPvdDis[loopsub];
        }
      }
    }

    double[][] dPvCaldPhi = new double[nbCalibrations][2 * nbPeriods];
    // Implementation note: Derivative of the calibration swaptions wrt the calibration parameters (multiplicative factor and additive term)
    double[][][] dPvCaldGamma = new double[nbCalibrations][][];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCaldGamma[loopcal] = METHOD_SWAPTION_LMM.presentValueLMMSensitivity(swaptionCalibration[loopcal], lmmBundle);
    }
    double[][] dPvCaldDis = new double[nbCalibrations][];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCaldDis[loopcal] = METHOD_SWAPTION_LMM.presentValueDDSensitivity(swaptionCalibration[loopcal], lmmBundle);
    }
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
        for (int loopsub = instrumentIndex.get(loopperiod * nbStrikes); loopsub < instrumentIndex.get((loopperiod + 1) * nbStrikes); loopsub++) {
          for (int loopfact = 0; loopfact < nbFact; loopfact++) {
            dPvCaldPhi[loopcal][loopperiod] += dPvCaldGamma[loopcal][loopsub][loopfact] * lmmParameters.getVolatility()[loopsub][loopfact];
            dPvCaldPhi[loopcal][nbPeriods + loopperiod] += dPvCaldDis[loopcal][loopsub];
          }
        }
      }
    }

    double[][] dPvCaldTheta = new double[nbCalibrations][3 * nbPeriods];
    // Implementation note: Derivative of the calibration swaptions wrt the SABR parameters as a unique array.
    for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
      for (int loopstrike = 0; loopstrike < nbStrikes; loopstrike++) {
        PresentValueSABRSensitivityDataBundle dPvCaldSABR = METHOD_SWAPTION_SABR.presentValueSABRSensitivity(swaptionCalibration[loopperiod * nbStrikes + loopstrike], curves);
        DoublesPair[] keySet = dPvCaldSABR.getAlpha().getMap().keySet().toArray(new DoublesPair[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][loopperiod] += dPvCaldSABR.getAlpha().getMap().get(keySet[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][nbPeriods + loopperiod] = dPvCaldSABR.getRho().getMap().get(keySet[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][2 * nbPeriods + loopperiod] = dPvCaldSABR.getNu().getMap().get(keySet[0]);
      }
    }

    double[][] dfdTheta = new double[2 * nbPeriods][3 * nbPeriods];
    // Implementation note: Derivative of f wrt the SABR parameters.
    for (int loopp = 0; loopp < 2 * nbPeriods; loopp++) {
      for (int loops = 0; loops < 3 * nbPeriods; loops++) {
        for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
          dfdTheta[loopp][loops] += -2 * dPvCaldPhi[loopcal][loopp] * dPvCaldTheta[loopcal][loops];
        }
      }
    }
    double[][] dfdPhi = new double[2 * nbPeriods][2 * nbPeriods];
    // Implementation note: Derivative of f wrt the calibration parameters. This is an approximation: the second order derivative part are ignored.
    for (int loopp1 = 0; loopp1 < 2 * nbPeriods; loopp1++) {
      for (int loopp2 = 0; loopp2 < 2 * nbPeriods; loopp2++) {
        for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
          dfdPhi[loopp1][loopp2] += 2 * dPvCaldPhi[loopcal][loopp1] * dPvCaldPhi[loopcal][loopp2];
        }
      }
    }

    DoubleMatrix2D dfdThetaMat = new DoubleMatrix2D(dfdTheta);
    DoubleMatrix2D dfdPhiMat = new DoubleMatrix2D(dfdPhi);
    DoubleMatrix2D dPhidThetaMat = (DoubleMatrix2D) ALGEBRA.scale(ALGEBRA.multiply(ALGEBRA.getInverse(dfdPhiMat), dfdThetaMat), -1.0);
    DoubleMatrix1D dPvdPhiMat = new DoubleMatrix1D(dPvdPhi);
    DoubleMatrix2D dPvdThetaMat = ALGEBRA.getTranspose(ALGEBRA.multiply(ALGEBRA.getTranspose(dPhidThetaMat), dPvdPhiMat));
    double[] dPvdTheta = dPvdThetaMat.getData()[0];

    // Storage in PresentValueSABRSensitivityDataBundle
    PresentValueSABRSensitivityDataBundle sensi = new PresentValueSABRSensitivityDataBundle();
    for (int loopp = 0; loopp < nbPeriods; loopp++) {
      DoublesPair expiryMaturity = new DoublesPair(swaptionCalibration[loopp * nbStrikes].getTimeToExpiry(), swaptionCalibration[loopp * nbStrikes].getMaturityTime());
      sensi.addAlpha(expiryMaturity, dPvdTheta[loopp]);
      sensi.addRho(expiryMaturity, dPvdTheta[nbPeriods + loopp]);
      sensi.addNu(expiryMaturity, dPvdTheta[2 * nbPeriods + loopp]);
    }
    return sensi;
  }

  public Triple<CurrencyAmount, PresentValueSABRSensitivityDataBundle, InterestRateCurveSensitivity> presentValueAndSensitivity(final SwaptionPhysicalFixedIbor swaption,
      final SABRInterestRateDataBundle curves) {
    Validate.notNull(swaption);
    Validate.notNull(curves);
    int nbStrikes = _strikeMoneyness.length;
    LiborMarketModelDisplacedDiffusionParameters lmmParameters = _parametersInit.copy();
    SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationObjective objective = new SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationObjective(lmmParameters);
    SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationEngine calibrationEngine = new SwaptionPhysicalLMMDDSuccessiveLeastSquareCalibrationEngine(objective, nbStrikes);
    SwaptionPhysicalFixedIbor[] swaptionCalibration = METHOD_BASKET.calibrationBasketFixedLegPeriod(swaption, _strikeMoneyness);
    calibrationEngine.addInstrument(swaptionCalibration, METHOD_SWAPTION_SABR);
    calibrationEngine.calibrate(curves);
    LiborMarketModelDisplacedDiffusionDataBundle lmmBundle = new LiborMarketModelDisplacedDiffusionDataBundle(lmmParameters, curves);

    // 1. PV 

    CurrencyAmount pv = METHOD_SWAPTION_LMM.presentValue(swaption, lmmBundle);

    int nbCalibrations = swaptionCalibration.length;
    int nbPeriods = nbCalibrations / nbStrikes;
    int nbFact = lmmParameters.getNbFactor();
    List<Integer> instrumentIndex = calibrationEngine.getInstrumentIndex();

    // 2. SABR sensitivities 

    double[] dPvdPhi = new double[2 * nbPeriods];
    // Implementation note: Derivative of the priced swaptions wrt the calibration parameters (multiplicative factor and additive term)
    double[][] dPvdGamma = METHOD_SWAPTION_LMM.presentValueLMMSensitivity(swaption, lmmBundle);
    double[] dPvdDis = METHOD_SWAPTION_LMM.presentValueDDSensitivity(swaption, lmmBundle);
    for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
      for (int loopsub = instrumentIndex.get(loopperiod * nbStrikes); loopsub < instrumentIndex.get((loopperiod + 1) * nbStrikes); loopsub++) {
        for (int loopfact = 0; loopfact < nbFact; loopfact++) {
          dPvdPhi[loopperiod] += dPvdGamma[loopsub][loopfact] * lmmParameters.getVolatility()[loopsub][loopfact];
          dPvdPhi[nbPeriods + loopperiod] += dPvdDis[loopsub];
        }
      }
    }

    double[][] dPvCaldPhi = new double[nbCalibrations][2 * nbPeriods];
    // Implementation note: Derivative of the calibration swaptions wrt the calibration parameters (multiplicative factor and additive term)
    double[][][] dPvCaldGamma = new double[nbCalibrations][][];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCaldGamma[loopcal] = METHOD_SWAPTION_LMM.presentValueLMMSensitivity(swaptionCalibration[loopcal], lmmBundle);
    }
    double[][] dPvCaldDis = new double[nbCalibrations][];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCaldDis[loopcal] = METHOD_SWAPTION_LMM.presentValueDDSensitivity(swaptionCalibration[loopcal], lmmBundle);
    }
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
        for (int loopsub = instrumentIndex.get(loopperiod * nbStrikes); loopsub < instrumentIndex.get((loopperiod + 1) * nbStrikes); loopsub++) {
          for (int loopfact = 0; loopfact < nbFact; loopfact++) {
            dPvCaldPhi[loopcal][loopperiod] += dPvCaldGamma[loopcal][loopsub][loopfact] * lmmParameters.getVolatility()[loopsub][loopfact];
            dPvCaldPhi[loopcal][nbPeriods + loopperiod] += dPvCaldDis[loopcal][loopsub];
          }
        }
      }
    }

    double[][] dPvCaldTheta = new double[nbCalibrations][3 * nbPeriods];
    // Implementation note: Derivative of the calibration swaptions wrt the SABR parameters as a unique array.
    for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
      for (int loopstrike = 0; loopstrike < nbStrikes; loopstrike++) {
        PresentValueSABRSensitivityDataBundle dPvCaldSABR = METHOD_SWAPTION_SABR.presentValueSABRSensitivity(swaptionCalibration[loopperiod * nbStrikes + loopstrike], curves);
        DoublesPair[] keySet = dPvCaldSABR.getAlpha().getMap().keySet().toArray(new DoublesPair[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][loopperiod] += dPvCaldSABR.getAlpha().getMap().get(keySet[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][nbPeriods + loopperiod] = dPvCaldSABR.getRho().getMap().get(keySet[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][2 * nbPeriods + loopperiod] = dPvCaldSABR.getNu().getMap().get(keySet[0]);
      }
    }

    double[][] dfdTheta = new double[2 * nbPeriods][3 * nbPeriods];
    // Implementation note: Derivative of f wrt the SABR parameters.
    for (int loopp = 0; loopp < 2 * nbPeriods; loopp++) {
      for (int loops = 0; loops < 3 * nbPeriods; loops++) {
        for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
          dfdTheta[loopp][loops] += -2 * dPvCaldPhi[loopcal][loopp] * dPvCaldTheta[loopcal][loops];
        }
      }
    }
    double[][] dfdPhi = new double[2 * nbPeriods][2 * nbPeriods];
    // Implementation note: Derivative of f wrt the calibration parameters. This is an approximation: the second order derivative part are ignored.
    for (int loopp1 = 0; loopp1 < 2 * nbPeriods; loopp1++) {
      for (int loopp2 = 0; loopp2 < 2 * nbPeriods; loopp2++) {
        for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
          dfdPhi[loopp1][loopp2] += 2 * dPvCaldPhi[loopcal][loopp1] * dPvCaldPhi[loopcal][loopp2];
        }
      }
    }

    DoubleMatrix2D dfdThetaMat = new DoubleMatrix2D(dfdTheta);
    DoubleMatrix2D dfdPhiMat = new DoubleMatrix2D(dfdPhi);
    DoubleMatrix2D dfdPhiInvMat = ALGEBRA.getInverse(dfdPhiMat);
    DoubleMatrix2D dPhidThetaMat = (DoubleMatrix2D) ALGEBRA.scale(ALGEBRA.multiply(dfdPhiInvMat, dfdThetaMat), -1.0);
    DoubleMatrix1D dPvdPhiMat = new DoubleMatrix1D(dPvdPhi);
    DoubleMatrix2D dPvdThetaMat = ALGEBRA.getTranspose(ALGEBRA.multiply(ALGEBRA.getTranspose(dPhidThetaMat), dPvdPhiMat));
    double[] dPvdTheta = dPvdThetaMat.getData()[0];

    // Storage in PresentValueSABRSensitivityDataBundle
    PresentValueSABRSensitivityDataBundle sensiSABR = new PresentValueSABRSensitivityDataBundle();
    for (int loopp = 0; loopp < nbPeriods; loopp++) {
      DoublesPair expiryMaturity = new DoublesPair(swaptionCalibration[loopp * nbStrikes].getTimeToExpiry(), swaptionCalibration[loopp * nbStrikes].getMaturityTime());
      sensiSABR.addAlpha(expiryMaturity, dPvdTheta[loopp]);
      sensiSABR.addRho(expiryMaturity, dPvdTheta[nbPeriods + loopp]);
      sensiSABR.addNu(expiryMaturity, dPvdTheta[2 * nbPeriods + loopp]);
    }

    // 3. Curve sensitivities 

    InterestRateCurveSensitivity[] dPvCalBasedC = new InterestRateCurveSensitivity[nbCalibrations];
    InterestRateCurveSensitivity[] dPvCalLmmdC = new InterestRateCurveSensitivity[nbCalibrations];
    InterestRateCurveSensitivity[] dPvCalDiffdC = new InterestRateCurveSensitivity[nbCalibrations];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCalBasedC[loopcal] = METHOD_SWAPTION_SABR.presentValueCurveSensitivity(swaptionCalibration[loopcal], curves);
      dPvCalLmmdC[loopcal] = METHOD_SWAPTION_LMM.presentValueCurveSensitivity(swaptionCalibration[loopcal], lmmBundle);
      dPvCalDiffdC[loopcal] = dPvCalBasedC[loopcal].plus(dPvCalLmmdC[loopcal].multiply(-1.0));
    }
    InterestRateCurveSensitivity[] dfdC = new InterestRateCurveSensitivity[2 * nbPeriods];
    // Implementation note: Derivative of f wrt the curves. This is an approximation: the second order derivative part are ignored.
    for (int loopp = 0; loopp < 2 * nbPeriods; loopp++) {
      dfdC[loopp] = new InterestRateCurveSensitivity();
      for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
        dfdC[loopp] = dfdC[loopp].plus(dPvCalDiffdC[loopcal].multiply(-2 * dPvCaldPhi[loopcal][loopp]));
      }
    }
    InterestRateCurveSensitivity[] dPhidC = new InterestRateCurveSensitivity[2 * nbPeriods];
    for (int loopp1 = 0; loopp1 < 2 * nbPeriods; loopp1++) {
      dPhidC[loopp1] = new InterestRateCurveSensitivity();
      for (int loopp2 = 0; loopp2 < 2 * nbPeriods; loopp2++) {
        dPhidC[loopp1] = dPhidC[loopp1].plus(dfdC[loopp2].multiply(-dfdPhiInvMat.getEntry(loopp1, loopp2)));
      }
    }
    InterestRateCurveSensitivity dPvdC = METHOD_SWAPTION_LMM.presentValueCurveSensitivity(swaption, lmmBundle);
    for (int loopp = 0; loopp < 2 * nbPeriods; loopp++) {
      dPvdC = dPvdC.plus(dPhidC[loopp].multiply(dPvdPhi[loopp]));
    }

    return new Triple<CurrencyAmount, PresentValueSABRSensitivityDataBundle, InterestRateCurveSensitivity>(pv, sensiSABR, dPvdC);
  }

  // TODO: curves sensitivity

}