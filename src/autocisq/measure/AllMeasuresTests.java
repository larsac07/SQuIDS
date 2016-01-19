package autocisq.measure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import autocisq.measure.maintainability.ClassTooManyChildrenTest;
import autocisq.measure.maintainability.ContinueOrBreakOutsideSwitchTest;
import autocisq.measure.maintainability.FileLOCTest;
import autocisq.measure.maintainability.FunctionCommentedOutInstructionsTest;
import autocisq.measure.maintainability.FunctionDuplicateTokensTest;
import autocisq.measure.maintainability.FunctionFanOutTest;
import autocisq.measure.maintainability.FunctionParametersTest;
import autocisq.measure.maintainability.HardCodedLiteralTest;
import autocisq.measure.maintainability.HorizontalLayersTest;
import autocisq.measure.maintainability.LayerSkippingCallTest;
import autocisq.measure.maintainability.MethodTooManyDataOrFileOperationsTest;
import autocisq.measure.maintainability.MethodDirectlyUsingFieldFromOtherClassTest;
import autocisq.measure.maintainability.VariableDeclaredPublicTest;
import autocisq.measure.reliability.EmptyExceptionHandlingBlockTest;

@RunWith(Suite.class)
@SuiteClasses({ EmptyExceptionHandlingBlockTest.class, HorizontalLayersTest.class, LayerSkippingCallTest.class,
		FileLOCTest.class, FunctionParametersTest.class, VariableDeclaredPublicTest.class, FunctionFanOutTest.class,
		MethodDirectlyUsingFieldFromOtherClassTest.class, FunctionCommentedOutInstructionsTest.class,
		ContinueOrBreakOutsideSwitchTest.class, HardCodedLiteralTest.class, ClassTooManyChildrenTest.class,
		MethodTooManyDataOrFileOperationsTest.class, FunctionDuplicateTokensTest.class })
public class AllMeasuresTests {

}
