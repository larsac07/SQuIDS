package autocisq.measure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import autocisq.measure.maintainability.FunctionPassing7OrMoreParametersTest;
import autocisq.measure.maintainability.FunctionWithFanOut10OrMoreTest;
import autocisq.measure.maintainability.HorizontalLayersTest;
import autocisq.measure.maintainability.LayerSkippingCallTest;
import autocisq.measure.maintainability.MethodDirectlyUsingFieldFromOtherClassTest;
import autocisq.measure.maintainability.MoreThan1000LOCTest;
import autocisq.measure.maintainability.VariableDeclaredPublicTest;
import autocisq.measure.reliability.EmptyExceptionHandlingBlockTest;

@RunWith(Suite.class)
@SuiteClasses({ EmptyExceptionHandlingBlockTest.class, HorizontalLayersTest.class, LayerSkippingCallTest.class,
		MoreThan1000LOCTest.class, FunctionPassing7OrMoreParametersTest.class, VariableDeclaredPublicTest.class,
		FunctionWithFanOut10OrMoreTest.class, MethodDirectlyUsingFieldFromOtherClassTest.class })
public class AllMeasuresTests {

}
