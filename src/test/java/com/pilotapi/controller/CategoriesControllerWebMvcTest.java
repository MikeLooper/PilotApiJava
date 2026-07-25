package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriesController.class)
class CategoriesControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void CategoriesControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryID(1);
        dto.setCategoryName("Beverages");
        when(categoryService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/categories/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].categoryID").value(1));
    }

    @Test
    void CategoriesControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryID(1);
        dto.setCategoryName("Beverages");
        when(categoryService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/categories/get/1").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryID").value(1));
    }

    @Test
    void CategoriesControllerWebMvcTest_add_returns_ok_Test() throws Exception {
        when(categoryService.add(any(CategoriesDto.class))).thenReturn(new AddResponseIntDto(100L));

        mockMvc.perform(post("/categories/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoryID\":1,\"categoryName\":\"Beverages\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void CategoriesControllerWebMvcTest_update_returns_ok_Test() throws Exception {
        doNothing().when(categoryService).update(any(CategoriesDto.class));

        mockMvc.perform(put("/categories/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoryID\":1,\"categoryName\":\"Beverages\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void CategoriesControllerWebMvcTest_delete_returns_no_content_Test() throws Exception {
        doNothing().when(categoryService).delete(1);

        mockMvc.perform(delete("/categories/delete/1").header("ApiVersion", "1"))
            .andExpect(status().isNoContent());
    }
}
