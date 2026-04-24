package com.uma.example.springuma.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;

public class MedicoControllerMockMvcIT extends AbstractIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Medico medico;

    @BeforeEach
    void setUp() {
        medico = new Medico();
        medico.setId(1L);
        medico.setDni("835");
        medico.setNombre("Miguel");
        medico.setEspecialidad("Ginecologia");
    }

    private void crearMedico(Medico medico) throws Exception {
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated());
    }
    @Test
    @DisplayName("Crear médico y recuperarlo por ID pasado por parámetro")
    void saveMedico_RecuperaMedicoPorId() throws Exception {
        crearMedico(medico);

        mockMvc.perform(get("/medico/" + medico.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.dni").value(medico.getDni()))
                .andExpect(jsonPath("$.nombre").value(medico.getNombre()));
    }

    @Test
    @DisplayName("Actualizar médico y verificar cambios por DNI")
    void saveMedico_UpdateMedico_GetByDni() throws Exception {
        crearMedico(medico);

        // Modificamos el nombre
        medico.setNombre("Miguel Angel");
        mockMvc.perform(put("/medico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isNoContent());

        // Recuperamos el médico actualizado
        mockMvc.perform(get("/medico/dni/" + medico.getDni()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Miguel Angel"));
    }

    @Test
    @DisplayName("Eliminar médico del sistema")
    void saveMedico_DeleteMedico() throws Exception {
        crearMedico(medico);

        // Lo borramos
        mockMvc.perform(delete("/medico/" + medico.getId()))
                .andExpect(status().isOk());
    }

}
