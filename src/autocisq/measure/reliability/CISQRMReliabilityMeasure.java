package autocisq.measure.reliability;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.measure.Measure;
import autocisq.models.Issue;

public abstract class CISQRMReliabilityMeasure extends Measure {

	public final static String QC = "CISQ Reliability";

	public CISQRMReliabilityMeasure(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public abstract List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits);

	@Override
	public abstract String getMeasureElement();

	@Override
	public String getQualityCharacteristic() {
		return QC;
	}

}
