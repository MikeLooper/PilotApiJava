package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.OrdersDto;
import com.pilotapi.service.OrderService;
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

@WebMvcTest(OrdersController.class)
class OrdersControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void OrdersControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        OrdersDto dto = new OrdersDto();
        dto.setOrderID(10);
        when(orderService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/orders/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].orderID").value(10));
    }

    @Test
    void OrdersControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        OrdersDto dto = new OrdersDto();
        dto.setOrderID(10);
        when(orderService.getById(10)).thenReturn(dto);

        mockMvc.perform(get("/orders/get/10").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderID").value(10));
    }

    @Test
    void OrdersControllerWebMvcTest_add_returns_ok_Test() throws Exception {
        when(orderService.add(any(OrdersDto.class))).thenReturn(new AddResponseIntDto(104L));

        mockMvc.perform(post("/orders/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderID\":10}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(104));
    }

    @Test
    void OrdersControllerWebMvcTest_update_returns_ok_Test() throws Exception {
        doNothing().when(orderService).update(any(OrdersDto.class));

        mockMvc.perform(put("/orders/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderID\":10}"))
            .andExpect(status().isOk());
    }

    @Test
    void OrdersControllerWebMvcTest_delete_returns_no_content_Test() throws Exception {
        doNothing().when(orderService).delete(10);

        mockMvc.perform(delete("/orders/delete/10").header("ApiVersion", "1"))
            .andExpect(status().isNoContent());
    }
}
