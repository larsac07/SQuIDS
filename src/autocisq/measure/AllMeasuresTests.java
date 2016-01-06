package autocisq.measure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EmptyExceptionHandlingBlockTest.class, HorizontalLayersTest.class, LayerSkippingCallTest.class,
		MoreThan1000LOCTest.class, FunctionPassing7OrMoreParametersTest.class, VariableDeclaredPublicTest.class,
		FunctionWithFanOut10OrMoreTest.class })
public class AllMeasuresTests {

}
