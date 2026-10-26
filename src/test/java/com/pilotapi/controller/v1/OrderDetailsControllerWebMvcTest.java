package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseOrderDetailsDto;
import com.pilotapi.dto.OrderDetailsDto;
import com.pilotapi.service.OrderDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import com.pilotapi.security.SecurityConfig;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderDetailsController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.active=false")
class OrderDetailsControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderDetailService orderDetailService;

    @Test
    void OrderDetailsControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setProductId(1);
        dto.setOrderId(10);
        dto.setDiscount(BigDecimal.valueOf(0.1));
        dto.setQuantity(3);
        dto.setUnitPrice(BigDecimal.valueOf(5.25));
        when(orderDetailService.getAll(0, 20)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/order-details/get-all").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productId").value(1));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_getAll_withPageParams_passesThemToService_Test() throws Exception {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setProductId(1);
        dto.setOrderId(10);
        dto.setDiscount(BigDecimal.valueOf(0.1));
        dto.setQuantity(3);
        dto.setUnitPrice(BigDecimal.valueOf(5.25));
        when(orderDetailService.getAll(2, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/order-details/get-all?page=2&pageSize=10").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productId").value(1));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setProductId(1);
        dto.setOrderId(10);
        dto.setDiscount(BigDecimal.valueOf(0.1));
        dto.setQuantity(3);
        dto.setUnitPrice(BigDecimal.valueOf(5.25));
        when(orderDetailService.getById(1, 10)).thenReturn(dto);

        mockMvc.perform(get("/v1/order-details/get/product/1/order/10").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(10));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_add_returns_created_Test() throws Exception {
        when(orderDetailService.add(any(OrderDetailsDto.class))).thenReturn(new AddResponseOrderDetailsDto(10, 1));

        mockMvc.perform(post("/v1/order-details/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"orderId\":10,\"discount\":0.1,\"quantity\":3,\"unitPrice\":5.25}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").value(10))
            .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        when(orderDetailService.update(any(OrderDetailsDto.class))).thenReturn(true);

        mockMvc.perform(put("/v1/order-details/update")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"orderId\":10,\"discount\":0.1,\"quantity\":3,\"unitPrice\":5.25}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void OrderDetailsControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        when(orderDetailService.delete(1, 10)).thenReturn(true);

        mockMvc.perform(delete("/v1/order-details/delete/product/1/order/10").header("X-Api-Version", "1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void OrderDetailsControllerWebMvcTest_add_withDiscountAboveOne_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/order-details/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"orderId\":10,\"discount\":1.5,\"quantity\":3,\"unitPrice\":5.25}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("discount: must be less than or equal to 1"));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_add_withZeroQuantity_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/order-details/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"orderId\":10,\"discount\":0,\"quantity\":0,\"unitPrice\":5.25}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void OrderDetailsControllerWebMvcTest_getById_withOrderIdBelowMinimum_returns_badRequest_Test() throws Exception {
        mockMvc.perform(get("/v1/order-details/get/product/1/order/0").header("X-Api-Version", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }
}
