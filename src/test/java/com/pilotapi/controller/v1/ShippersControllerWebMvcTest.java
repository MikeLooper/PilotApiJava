package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.ShippersDto;
import com.pilotapi.service.ShipperService;
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

@WebMvcTest(ShippersController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.active=false")
class ShippersControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShipperService shipperService;

    @Test
    void ShippersControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        ShippersDto dto = new ShippersDto();
        dto.setShipperID(1);
        dto.setCompanyName("Speedy Express");
        when(shipperService.getAll(0, 20)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/shippers/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].shipperID").value(1));
    }

    @Test
    void ShippersControllerWebMvcTest_getAll_withPageParams_passesThemToService_Test() throws Exception {
        ShippersDto dto = new ShippersDto();
        dto.setShipperID(1);
        dto.setCompanyName("Speedy Express");
        when(shipperService.getAll(2, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/shippers/get-all?page=2&pageSize=10").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].shipperID").value(1));
    }

    @Test
    void ShippersControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        ShippersDto dto = new ShippersDto();
        dto.setShipperID(1);
        dto.setCompanyName("Speedy Express");
        when(shipperService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/v1/shippers/get/1").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shipperID").value(1));
    }

    @Test
    void ShippersControllerWebMvcTest_add_returns_created_Test() throws Exception {
        when(shipperService.add(any(ShippersDto.class))).thenReturn(new AddResponseIntDto(106L));

        mockMvc.perform(post("/v1/shippers/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"shipperID\":1,\"companyName\":\"Speedy Express\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(106));
    }

    @Test
    void ShippersControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        when(shipperService.update(any(ShippersDto.class))).thenReturn(true);

        mockMvc.perform(put("/v1/shippers/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"shipperID\":1,\"companyName\":\"Speedy Express\"}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void ShippersControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        when(shipperService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/v1/shippers/delete/1").header("ApiVersion", "1"))
            .andExpect(status().isNoContent());
    }
}
