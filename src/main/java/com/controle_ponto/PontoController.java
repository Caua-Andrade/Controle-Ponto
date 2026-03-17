package com.controle_ponto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PontoController {

    private static final int HORAS_OBRIGATORIAS = 6;

    @GetMapping("/calcular")
    public ResponseEntity<Map<String, String>> calcular(
            @RequestParam String inicioTrabalho,
            @RequestParam String inicioPausa,
            @RequestParam String fimPausa) {

        Map<String, String> resposta = new HashMap<>();

        try {
            LocalTime inicio      = LocalTime.parse(inicioTrabalho);
            LocalTime pausaInicio = LocalTime.parse(inicioPausa);
            LocalTime pausaFim    = LocalTime.parse(fimPausa);

            if (!pausaInicio.isAfter(inicio)) {
                resposta.put("erro", "O início da pausa deve ser após o início do trabalho.");
                return ResponseEntity.badRequest().body(resposta);
            }

            if (!pausaFim.isAfter(pausaInicio)) {
                resposta.put("erro", "O fim da pausa deve ser após o início da pausa.");
                return ResponseEntity.badRequest().body(resposta);
            }

            Duration duracaoPausa = Duration.between(pausaInicio, pausaFim);
            LocalTime saida = inicio.plusHours(HORAS_OBRIGATORIAS).plus(duracaoPausa);

            Duration trabalhadoAntesDaPausa = Duration.between(inicio, pausaInicio);

            long minutosPausa = duracaoPausa.toMinutes();
            long horasPausa   = minutosPausa / 60;
            long minRestantes = minutosPausa % 60;

            String pausaFormatada = horasPausa > 0
                    ? horasPausa + "h " + minRestantes + "min"
                    : minRestantes + "min";

            resposta.put("saida", saida.toString());
            resposta.put("duracaoPausa", pausaFormatada);
            resposta.put("trabalhadoAntesPausa",
                    trabalhadoAntesDaPausa.toHours() + "h " +
                            (trabalhadoAntesDaPausa.toMinutes() % 60) + "min");

        } catch (DateTimeParseException e) {
            resposta.put("erro", "Formato inválido. Use HH:mm (ex: 08:00).");
            return ResponseEntity.badRequest().body(resposta);
        }

        return ResponseEntity.ok(resposta);
    }
}