package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.OrdersDto;
import com.pilotapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import com.pilotapi.security.SecurityConfig;
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

@WebMvcTest(OrdersController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.active=false")
class OrdersControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void OrdersControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        OrdersDto dto = new OrdersDto();
        dto.setOrderId(10);
        when(orderService.getAll(0, 20)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/orders/get-all").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].orderId").value(10));
    }

    @Test
    void OrdersControllerWebMvcTest_getAll_withPageParams_passesThemToService_Test() throws Exception {
        OrdersDto dto = new OrdersDto();
        dto.setOrderId(10);
        when(orderService.getAll(2, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/orders/get-all?page=2&pageSize=10").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].orderId").value(10));
    }

    @Test
    void OrdersControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        OrdersDto dto = new OrdersDto();
        dto.setOrderId(10);
        when(orderService.getById(10)).thenReturn(dto);

        mockMvc.perform(get("/v1/orders/get/10").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(10));
    }

    @Test
    void OrdersControllerWebMvcTest_add_returns_created_Test() throws Exception {
        when(orderService.add(any(OrdersDto.class))).thenReturn(new AddResponseIntDto(104L));

        mockMvc.perform(post("/v1/orders/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderId\":10}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(104));
    }

    @Test
    void OrdersControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        when(orderService.update(any(OrdersDto.class))).thenReturn(true);

        mockMvc.perform(put("/v1/orders/update")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderId\":10}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void OrdersControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        when(orderService.delete(10)).thenReturn(true);

        mockMvc.perform(delete("/v1/orders/delete/10").header("X-Api-Version", "1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void OrdersControllerWebMvcTest_add_withCustomerIdOfWrongLength_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/orders/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":\"VIN\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void OrdersControllerWebMvcTest_add_withShipViaBelowMinimum_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/orders/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"shipVia\":0}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void OrdersControllerWebMvcTest_add_withFreightWithMoreThanFourDecimals_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/orders/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"freight\":32.38001}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"orderDate", "requiredDate", "shippedDate"})
    void OrdersControllerWebMvcTest_add_withDateBeforeSqlServerMinimum_returns_badRequest_Test(String field) throws Exception {
        mockMvc.perform(post("/v1/orders/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"" + field + "\":\"0001-01-01T00:00:00\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }
}
