package gift.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.model.Category;
import gift.model.Member;
import gift.model.Product;
import gift.model.Role;
import gift.response.category.CategoryResponse;
import gift.response.product.ProductResponse;
import gift.service.CategoryService;
import gift.service.OptionsService;
import gift.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProxyAuthTest {

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;
    @MockBean
    private OptionsService optionsService;
    @MockBean
    private CategoryService categoryService;

    private String token;

    @BeforeEach
    void setUp() {
        token = tokenProvider.generateToken(
            new Member("abc123@a.com", "1234", Role.ROLE_ADMIN));
    }

    @DisplayName("인증이 필요한 Uri: 토큰 없는 요청에 대한 401 정상 반환 테스트")
    @Test
    void needAuthenticateNotToken() throws Exception {
        //given
        Product product = demoProduct(1L);
        given(productService.getProduct(any(Long.class)))
            .willReturn(product);

        //when then
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/products/{id}/options", product.getId()))
            .andExpect(status().isUnauthorized())
            .andDo(print());
    }

    @DisplayName("인증이 필요한 Uri: 토큰 있는 요청에 대한 정상 반환 테스트")
    @Test
    void needAuthenticateWithToken() throws Exception {
        //given
        Product product = demoProduct(1L);
        ProductResponse productResponse = ProductResponse.createProductResponse(product);
        given(productService.getProduct(any(Long.class)))
            .willReturn(product);

        //when //then
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/products/{productId}", product.getId())
                    .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("인증이 필요없는 Uri: 토큰 없는 요청에 대한 200 정상 반환 테스트")
    @Test
    void notNeedAuthenticate() throws Exception {
        //given
        int dataCounts = 5;
        List<CategoryResponse> categories = new ArrayList<>();
        LongStream.range(0, dataCounts)
            .forEach(i -> {
                Category category = new Category(i + 1, "카테고리 " + (i + 1) , "color", "imageUrl", "description");
                categories.add(CategoryResponse.createCategoryResponse(category));
            });

        given(categoryService.getAllCategories())
            .willReturn(categories);

        //when then
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andDo(print());
    }

    private static Product demoProduct(Long id) {
        return new Product(id, "상품", 1000, "http://a.com", new Category(1L, "카테고리"));
    }
}
