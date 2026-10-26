package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseStringDto;
import com.pilotapi.dto.CustomersDto;
import com.pilotapi.service.CustomerService;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(CustomersController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.active=false")
class CustomersControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    void CustomersControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        CustomersDto dto = new CustomersDto();
        dto.setCustomerId("ALFKI");
        dto.setCompanyName("Alfreds");
        when(customerService.getAll(0, 20)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/customers/get-all").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].customerId").value("ALFKI"));
    }

    @Test
    void CustomersControllerWebMvcTest_getAll_withPageParams_passesThemToService_Test() throws Exception {
        CustomersDto dto = new CustomersDto();
        dto.setCustomerId("ALFKI");
        dto.setCompanyName("Alfreds");
        when(customerService.getAll(2, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/customers/get-all?page=2&pageSize=10").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].customerId").value("ALFKI"));
    }

    @Test
    void CustomersControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        CustomersDto dto = new CustomersDto();
        dto.setCustomerId("ALFKI");
        dto.setCompanyName("Alfreds");
        when(customerService.getById("ALFKI")).thenReturn(dto);

        mockMvc.perform(get("/v1/customers/get/ALFKI").header("X-Api-Version", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value("ALFKI"));
    }

    @Test
    void CustomersControllerWebMvcTest_add_returns_created_Test() throws Exception {
        when(customerService.add(any(CustomersDto.class))).thenReturn(new AddResponseStringDto("ALFKI"));

        mockMvc.perform(post("/v1/customers/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":\"ALFKI\",\"companyName\":\"Alfreds\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("ALFKI"));
    }

    @Test
    void CustomersControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        when(customerService.update(any(CustomersDto.class))).thenReturn(true);

        mockMvc.perform(put("/v1/customers/update")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":\"ALFKI\",\"companyName\":\"Alfreds\"}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void CustomersControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        when(customerService.delete("ALFKI")).thenReturn(true);

        mockMvc.perform(delete("/v1/customers/delete/ALFKI").header("X-Api-Version", "1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void CustomersControllerWebMvcTest_getById_withIdOfWrongLength_returns_badRequest_Test() throws Exception {
        mockMvc.perform(get("/v1/customers/get/ALFKIX").header("X-Api-Version", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("customerId: size must be between 5 and 5"));
    }

    @Test
    void CustomersControllerWebMvcTest_add_withCustomerIdOfWrongLength_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/customers/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":\"ALF\",\"companyName\":\"Alfreds\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void CustomersControllerWebMvcTest_add_withCompanyNameTooLong_returns_badRequest_Test() throws Exception {
        mockMvc.perform(post("/v1/customers/add")
                .header("X-Api-Version", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":\"ALFKI\",\"companyName\":\"" + "x".repeat(41) + "\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }
}
