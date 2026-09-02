package com.pilotapi.controller.v1;

import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.security.SecurityConfig;
import com.pilotapi.service.CategoryService;
import com.pilotapi.testing.TestJwtSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runs with security active (the application.yml default), verifying the
 * enforce-vs-allow decision end to end through {@link CategoriesController} as a
 * representative domain endpoint. All 8 domain controllers share the same
 * CRUD/method shape, so authorization behavior is uniform across them; the
 * branching logic itself is exhaustively covered by SecurityHelperTest and
 * AuthEnforcementFilterTest.
 */
@WebMvcTest(CategoriesController.class)
@Import(SecurityConfig.class)
class CategoriesControllerSecurityWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void CategoriesControllerSecurityWebMvcTest_getAll_withoutToken_returns401_Test() throws Exception {
        mockMvc.perform(get("/v1/categories/get-all"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void CategoriesControllerSecurityWebMvcTest_getAll_withReadOnlyRoleToken_returns200_Test() throws Exception {
        when(jwtDecoder.decode("reader-token")).thenReturn(TestJwtSupport.jwtForUser("reader_user"));
        when(categoryService.getAll()).thenReturn(List.of(new CategoriesDto()));

        mockMvc.perform(get("/v1/categories/get-all").header("Authorization", "Bearer reader-token"))
            .andExpect(status().isOk());
    }

    @Test
    void CategoriesControllerSecurityWebMvcTest_delete_withReadOnlyRoleToken_returns403_Test() throws Exception {
        when(jwtDecoder.decode("reader-token")).thenReturn(TestJwtSupport.jwtForUser("reader_user"));

        mockMvc.perform(delete("/v1/categories/delete/1").header("Authorization", "Bearer reader-token"))
            .andExpect(status().isForbidden());
    }

    @Test
    void CategoriesControllerSecurityWebMvcTest_delete_withAdminRoleToken_returns204_Test() throws Exception {
        when(jwtDecoder.decode("admin-token")).thenReturn(TestJwtSupport.jwtForUser("working_admin_user"));
        when(categoryService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/v1/categories/delete/1").header("Authorization", "Bearer admin-token"))
            .andExpect(status().isNoContent());
    }
}
