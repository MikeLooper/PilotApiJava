package com.pilotapi.controller;

import com.pilotapi.dto.AboutResponseDto;
import com.pilotapi.service.SystemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemController.class)
class SystemControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SystemService systemService;

    @Test
    void SystemControllerWebMvcTest_healthcheck_returns_ok_string_Test() throws Exception {
        when(systemService.healthcheck()).thenReturn("OK");

        mockMvc.perform(get("/healthcheck").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(content().string("OK"));
    }

    @Test
    void SystemControllerWebMvcTest_about_returns_ok_response_Test() throws Exception {
        AboutResponseDto dto = new AboutResponseDto();
        dto.setApiVersion("1");
        dto.setName("PilotApiJava");
        dto.setBuildVersion("0.0.1");
        when(systemService.about(true, "1")).thenReturn(dto);

        mockMvc.perform(get("/about")
                .header("ApiVersion", "1")
                .param("show-details", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("PilotApiJava"));
    }
}
