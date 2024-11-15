package ru.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.InputEventDto;
import ru.practicum.dto.OutputStatsDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsClient extends BaseClient {

    @Autowired
    public StatisticsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void save(InputEventDto dto) {
        post("/hit", dto);
    }

    public List<OutputStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        String uri = String.join(",", uris);
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uri,
                "unique", unique
        );
        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        List<OutputStatsDto> stats;
        ObjectMapper mapper = new ObjectMapper();
        try {
            stats = List.of(mapper.readValue(mapper.writeValueAsString(response.getBody()), OutputStatsDto[].class));
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
        return stats;
    }
}
