package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.ProductsDto;
import com.pilotapi.service.ProductService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductsController.class)
class ProductsControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void ProductsControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        ProductsDto dto = new ProductsDto();
        dto.setProductID(1);
        dto.setProductName("Chai");
        dto.setReorderLevel(1);
        dto.setUnitsInStock(10);
        dto.setUnitsOnOrder(0);
        when(productService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/products/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productID").value(1));
    }

    @Test
    void ProductsControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        ProductsDto dto = new ProductsDto();
        dto.setProductID(1);
        dto.setProductName("Chai");
        dto.setReorderLevel(1);
        dto.setUnitsInStock(10);
        dto.setUnitsOnOrder(0);
        when(productService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/products/get/1").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productID").value(1));
    }

    @Test
    void ProductsControllerWebMvcTest_add_returns_ok_Test() throws Exception {
        when(productService.add(any(ProductsDto.class))).thenReturn(new AddResponseIntDto(105L));

        mockMvc.perform(post("/products/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\":1,\"productName\":\"Chai\",\"reorderLevel\":1,\"unitsInStock\":10,\"unitsOnOrder\":0}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(105));
    }

    @Test
    void ProductsControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        when(productService.update(any(ProductsDto.class))).thenReturn(true);

        mockMvc.perform(put("/products/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\":1,\"productName\":\"Chai\",\"reorderLevel\":1,\"unitsInStock\":10,\"unitsOnOrder\":0}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void ProductsControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        when(productService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/products/delete/1").header("ApiVersion", "1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void ProductsControllerWebMvcTest_add_returns_bad_request_for_invalid_payload_Test() throws Exception {
        mockMvc.perform(post("/products/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
