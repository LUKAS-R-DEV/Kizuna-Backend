package Kizuna_core_service.shared.util.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/server-time")
public class ServerTimeControler {

    @GetMapping()
    public ResponseEntity<String> getServerTime() {
        String time = LocalDateTime.now().toString();
        return ResponseEntity.ok(time);
    }
}
