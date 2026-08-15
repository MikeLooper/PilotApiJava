package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.EmployeesDto;
import com.pilotapi.service.EmployeeService;
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

@WebMvcTest(EmployeesController.class)
class EmployeesControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void EmployeesControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        EmployeesDto dto = new EmployeesDto();
        dto.setEmployeeID(1);
        dto.setFirstName("Nancy");
        dto.setLastName("Davolio");
        when(employeeService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/employees/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].employeeID").value(1));
    }

    @Test
    void EmployeesControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        EmployeesDto dto = new EmployeesDto();
        dto.setEmployeeID(1);
        dto.setFirstName("Nancy");
        dto.setLastName("Davolio");
        when(employeeService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/employees/get/1").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.employeeID").value(1));
    }

    @Test
    void EmployeesControllerWebMvcTest_add_returns_ok_Test() throws Exception {
        when(employeeService.add(any(EmployeesDto.class))).thenReturn(new AddResponseIntDto(102L));

        mockMvc.perform(post("/employees/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeID\":1,\"firstName\":\"Nancy\",\"lastName\":\"Davolio\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(102));
    }

    @Test
    void EmployeesControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        doNothing().when(employeeService).update(any(EmployeesDto.class));

        mockMvc.perform(put("/employees/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeID\":1,\"firstName\":\"Nancy\",\"lastName\":\"Davolio\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Employee updated"));
    }

    @Test
    void EmployeesControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        doNothing().when(employeeService).delete(1);

        mockMvc.perform(delete("/employees/delete/1").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Employee deleted"));
    }
}
