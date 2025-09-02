package com.javarush.jira.profile.internal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.jira.profile.ProfileTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileRestControllerTest {

    private static final String REST_URL = "/api/profile";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    void getProfile() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(USER_PROFILE_TO)));
    }

    @Test
    void getProfileUnauthorized() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    void updateProfile() throws Exception {
        ProfileTo updatedTo = getUpdatedTo();
        mockMvc.perform(put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTo)))
                .andExpect(status().isNoContent());

        // Проверяем, что профиль обновился
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedTo)));
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    void updateProfileInvalid() throws Exception {
        ProfileTo invalidTo = getInvalidTo();
        mockMvc.perform(put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTo)))
                .andExpect(status().isUnprocessableEntity());
    }
}