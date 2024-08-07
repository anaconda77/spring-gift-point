package gift.auth;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@Component
public class PathMethodContainer {

    private final PathMatcher pathMatcher;
    private final List<RequestPathImpl> includePathPattern;
    private final List<RequestPathImpl> excludePathPattern;

    public PathMethodContainer() {
        this.pathMatcher = new AntPathMatcher();
        this.includePathPattern = new ArrayList<>();
        this.excludePathPattern = new ArrayList<>();
    }

    public boolean notIncludedPath(String targetPath, String pathMethod) {
        boolean excludePattern = excludePathPattern.stream()
            .anyMatch(requestPath -> anyMatchPathPattern(targetPath, pathMethod, requestPath));

        boolean includePattern = includePathPattern.stream()
            .anyMatch(requestPath -> anyMatchPathPattern(targetPath, pathMethod, requestPath));

        return excludePattern || !includePattern;
    }

    private boolean anyMatchPathPattern(String targetPath, String pathMethod, RequestPathImpl requestPath) {
        return pathMatcher.match(requestPath.getPathPattern(), targetPath) &&
            requestPath.matchesMethod(pathMethod);
    }

    public void includePathPattern(List<String> targetPaths, PathMethod pathMethod) {
        this.includePathPattern.addAll(targetPaths.stream()
            .map( t -> new RequestPathImpl(t, pathMethod))
            .toList());
    }

    public void excludePathPattern(List<String> targetPaths, PathMethod pathMethod) {
        this.excludePathPattern.addAll(targetPaths.stream()
            .map(t -> new RequestPathImpl(t, pathMethod))
            .toList());
    }
}
