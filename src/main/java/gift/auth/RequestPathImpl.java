package gift.auth;

import java.util.List;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;

public class RequestPathImpl implements RequestPath {
    String targetPath;
    PathMethod pathMethod;

    public RequestPathImpl(String targetPath, PathMethod pathMethod) {
        this.targetPath = targetPath;
        this.pathMethod = pathMethod;
    }

    public String getPathPattern() {
        return targetPath;
    }

    public boolean matchesMethod(String method) {
        return pathMethod.equals(PathMethod.ANY)  || pathMethod.toString().equals(method);
    }

    @Override
    public PathContainer contextPath() {
        return null;
    }

    @Override
    public PathContainer pathWithinApplication() {
        return null;
    }

    @Override
    public RequestPath modifyContextPath(String contextPath) {
        return null;
    }

    @Override
    public String value() {
        return "";
    }

    @Override
    public List<Element> elements() {
        return List.of();
    }
}
