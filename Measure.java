public interface Measure {
    public List<Issue> analyzeNode(Node node, String fileString);
}

public class FilesMoreThan1000LOC implements Measure {

    @Override
    public Issue analyzeNode(Node node, String fileString) {
        if (node instanceof CompilationUnit && node.getEndLine() > 1000) {
            return new Issue();
        } else {
            return null;
        }
    }
}