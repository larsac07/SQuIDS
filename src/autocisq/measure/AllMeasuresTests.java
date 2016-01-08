package autocisq.measure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import autocisq.measure.maintainability.FunctionParametersTest;
import autocisq.measure.maintainability.FunctionFanOutTest;
import autocisq.measure.maintainability.HorizontalLayersTest;
import autocisq.measure.maintainability.LayerSkippingCallTest;
import autocisq.measure.maintainability.MethodDirectlyUsingFieldFromOtherClassTest;
import autocisq.measure.maintainability.FileLOCTest;
import autocisq.measure.maintainability.VariableDeclaredPublicTest;
import autocisq.measure.reliability.EmptyExceptionHandlingBlockTest;

@RunWith(Suite.class)
@SuiteClasses({ EmptyExceptionHandlingBlockTest.class, HorizontalLayersTest.class, LayerSkippingCallTest.class,
		FileLOCTest.class, FunctionParametersTest.class, VariableDeclaredPublicTest.class,
		FunctionFanOutTest.class, MethodDirectlyUsingFieldFromOtherClassTest.class })
public class AllMeasuresTests {

}
