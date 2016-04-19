package autocisq.measure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import autocisq.measure.maintainability.ClassCouplingTest;
import autocisq.measure.maintainability.ClassInheritanceLevelTest;
import autocisq.measure.maintainability.ClassChildrenTest;
import autocisq.measure.maintainability.ContinueOrBreakOutsideSwitchTest;
import autocisq.measure.maintainability.CyclicCallBetweenPackagesTest;
import autocisq.measure.maintainability.FileDuplicateTokensTest;
import autocisq.measure.maintainability.FileLOCTest;
import autocisq.measure.maintainability.HardCodedLiteralTest;
import autocisq.measure.maintainability.HorizontalLayersTest;
import autocisq.measure.maintainability.IndexModifiedWithinLoopTest;
import autocisq.measure.maintainability.LayerSkippingCallTest;
import autocisq.measure.maintainability.MethodCommentedOutInstructionsTest;
import autocisq.measure.maintainability.MethodCyclomaticComplexityTest;
import autocisq.measure.maintainability.MethodDirectlyUsingFieldFromOtherClassTest;
import autocisq.measure.maintainability.MethodFanOutTest;
import autocisq.measure.maintainability.MethodParametersTest;
import autocisq.measure.maintainability.MethodDataOrFileOperationsTest;
import autocisq.measure.maintainability.MethodUnreachableTest;
import autocisq.measure.maintainability.VariableDeclaredPublicTest;
import autocisq.measure.reliability.EmptyExceptionHandlingBlockTest;

@RunWith(Suite.class)
@SuiteClasses({ EmptyExceptionHandlingBlockTest.class, HorizontalLayersTest.class, LayerSkippingCallTest.class,
		FileLOCTest.class, MethodParametersTest.class, VariableDeclaredPublicTest.class, MethodFanOutTest.class,
		MethodDirectlyUsingFieldFromOtherClassTest.class, MethodCommentedOutInstructionsTest.class,
		ContinueOrBreakOutsideSwitchTest.class, HardCodedLiteralTest.class, ClassChildrenTest.class,
		MethodDataOrFileOperationsTest.class, FileDuplicateTokensTest.class,
		MethodCyclomaticComplexityTest.class, MethodUnreachableTest.class, ClassInheritanceLevelTest.class,
		ClassCouplingTest.class, CyclicCallBetweenPackagesTest.class, IndexModifiedWithinLoopTest.class })

public class AllMeasuresTests {

}
