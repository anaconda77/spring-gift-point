package gift.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ProxyAuthApiInterceptor implements HandlerInterceptor {

    private AuthApiInterceptor authApiInterceptor;
    private PathMethodContainer pathMethodContainer;

    public ProxyAuthApiInterceptor(AuthApiInterceptor authApiInterceptor) {
        this.authApiInterceptor = authApiInterceptor;
        this.pathMethodContainer = new PathMethodContainer();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        if (pathMethodContainer.notIncludedPath(request.getRequestURI(), request.getMethod())) {
            return true;
        }

        return authApiInterceptor.preHandle(request, response, handler);
    }


    public ProxyAuthApiInterceptor addPathPatterns(PathMethod pathMethod, String... pathPatterns) {
        pathMethodContainer.includePathPattern(Arrays.asList(pathPatterns), pathMethod);
        return this;
    }

    public ProxyAuthApiInterceptor excludePathPatterns(PathMethod pathMethod, String... pathPatterns) {
        pathMethodContainer.excludePathPattern(Arrays.asList(pathPatterns), pathMethod);
        return this;
    }

}
