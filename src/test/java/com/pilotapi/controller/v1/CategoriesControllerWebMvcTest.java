package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.pilotapi.security.SecurityConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runs with security inactive (warn-only) so the pre-existing CRUD assertions here
 * do not need to supply a bearer token. Active-mode (block) behavior is covered by
 * {@link CategoriesControllerSecurityWebMvcTest}.
 */
@WebMvcTest(CategoriesController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.active=false")
class CategoriesControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void CategoriesControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryId(1);
        dto.setCategoryName("Beverages");
        when(categoryService.getAll(0, 20)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/categories/get-all").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].categoryId").value(1));
    }

    @Test
    void CategoriesControllerWebMvcTest_getAll_withPageParams_passesThemToService_Test() throws Exception {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryId(1);
        dto.setCategoryName("Beverages");
        when(categoryService.getAll(2, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/categories/get-all?page=2&pageSize=10").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].categoryId").value(1));
    }

    @Test
    void CategoriesControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryId(1);
        dto.setCategoryName("Beverages");
        when(categoryService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/v1/categories/get/1").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryId").value(1));
    }

    @Test
    void CategoriesControllerWebMvcTest_add_returns_created_Test() throws Exception {
        when(categoryService.add(any(CategoriesDto.class))).thenReturn(new AddResponseIntDto(100L));

        mockMvc.perform(post("/v1/categories/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoryId\":1,\"categoryName\":\"Beverages\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void CategoriesControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        when(categoryService.update(any(CategoriesDto.class))).thenReturn(true);

        mockMvc.perform(put("/v1/categories/update")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoryId\":1,\"categoryName\":\"Beverages\"}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void CategoriesControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        when(categoryService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/v1/categories/delete/1").header("X-Api-Version", "1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void CategoriesControllerWebMvcTest_getById_echoesApiVersionAndCorrelationIdHeaders_Test() throws Exception {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryId(1);
        when(categoryService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/v1/categories/get/1")
                .header("X-Api-Version", "1.2.3")
                .header("X-Correlation-Id", "7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Api-Version", "1.2.3"))
            .andExpect(header().string("X-Correlation-Id", "7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b"));
    }

    @Test
    void CategoriesControllerWebMvcTest_getById_withInvalidCorrelationId_returns_badRequest_Test() throws Exception {
        mockMvc.perform(get("/v1/categories/get/1").header("X-Correlation-Id", "not-a-uuid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("about:blank"))
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void CategoriesControllerWebMvcTest_getById_withNonNumericId_returns_badRequest_Test() throws Exception {
        mockMvc.perform(get("/v1/categories/get/abc").header("X-Api-Version", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.instance").value("/v1/categories/get/abc"));
    }

    @Test
    void CategoriesControllerWebMvcTest_add_withMalformedBody_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/categories/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoryName\":"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void CategoriesControllerWebMvcTest_add_withMissingRequiredField_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/categories/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"No name\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void CategoriesControllerWebMvcTest_getAll_withoutToken_whenInactive_allowsThrough_withWarningHeader_Test() throws Exception {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryId(1);
        when(categoryService.getAll(0, 20)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/categories/get-all").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Warning"));
    }
}
