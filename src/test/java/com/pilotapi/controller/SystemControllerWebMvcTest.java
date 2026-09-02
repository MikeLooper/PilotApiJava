package com.pilotapi.controller;

import com.pilotapi.dto.AboutResponseDto;
import com.pilotapi.service.SystemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.pilotapi.security.SecurityConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemController.class)
@Import(SecurityConfig.class)
class SystemControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SystemService systemService;

    @Test
    void SystemControllerWebMvcTest_healthcheck_returns_json_string_Test() throws Exception {
        when(systemService.healthcheck()).thenReturn("OK");

        mockMvc.perform(get("/healthcheck")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().exists("Date"))
            .andExpect(header().string("ApiVersion", "1.0.0"))
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(content().json("\"OK\""));
    }

    @Test
    void SystemControllerWebMvcTest_about_returns_ok_response_Test() throws Exception {
        AboutResponseDto dto = new AboutResponseDto();
        dto.setApiVersion("1.0.0");
        dto.setName("PilotApiJava");
        dto.setBuildVersion("0.0.1");
        when(systemService.about(true, "1.0.0")).thenReturn(dto);

        mockMvc.perform(get("/about")
                .accept(MediaType.APPLICATION_JSON)
                .param("show-details", "true"))
            .andExpect(status().isOk())
            .andExpect(header().string("ApiVersion", "1.0.0"))
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("$.name").value("PilotApiJava"));

        verify(systemService).about(true, "1.0.0");
    }
}
