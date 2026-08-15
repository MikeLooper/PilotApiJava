package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.OrderDetailsDto;
import com.pilotapi.service.OrderDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(OrderDetailsController.class)
class OrderDetailsControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderDetailService orderDetailService;

    @Test
    void OrderDetailsControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setProductID(1);
        dto.setOrderID(10);
        dto.setDiscount(0.1f);
        dto.setQuantity(3);
        dto.setUnitPrice(BigDecimal.valueOf(5.25));
        when(orderDetailService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/order-details/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productID").value(1));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setProductID(1);
        dto.setOrderID(10);
        dto.setDiscount(0.1f);
        dto.setQuantity(3);
        dto.setUnitPrice(BigDecimal.valueOf(5.25));
        when(orderDetailService.getById(1, 10)).thenReturn(dto);

        mockMvc.perform(get("/order-details/get/product/1/order/10").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderID").value(10));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_add_returns_ok_Test() throws Exception {
        when(orderDetailService.add(any(OrderDetailsDto.class))).thenReturn(new AddResponseIntDto(103L));

        mockMvc.perform(post("/order-details/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\":1,\"orderID\":10,\"discount\":0.1,\"quantity\":3,\"unitPrice\":5.25}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(103));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        doNothing().when(orderDetailService).update(any(OrderDetailsDto.class));

        mockMvc.perform(put("/order-details/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\":1,\"orderID\":10,\"discount\":0.1,\"quantity\":3,\"unitPrice\":5.25}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Order detail updated"));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        doNothing().when(orderDetailService).delete(1, 10);

        mockMvc.perform(delete("/order-details/delete/product/1/order/10").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Order detail deleted"));
    }
}
