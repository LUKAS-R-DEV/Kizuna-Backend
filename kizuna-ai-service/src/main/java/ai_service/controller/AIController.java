package ai_service.controller;

import ai_service.dto.ChatRequestDto;
import ai_service.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/AI")
@AllArgsConstructor
public class AIController {
    private final FileStorageService fileStorageService;
    private final ChatModel chatModel;




    @PostMapping("/process")
    public ResponseEntity<Map<String,String>> processReport(@RequestParam("File") MultipartFile file){
        try{
            String content = fileStorageService.extractText(file);
            return ResponseEntity.ok(Map.of("content", content));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String,String>> chatWithAI(@RequestBody ChatRequestDto requestDto){
        try{
            String systemMessageText = """
            Você é o assistente virtual do sistema KIZUNA.
            Sua função é analisar dados industriais e responder perguntas baseadas no relatório fornecido.
            Seja técnico, direto e use tabelas ou listas se necessário.
            Contexto do Relatório:
            """ + requestDto.context();

            SystemMessage systemMessage = new SystemMessage(systemMessageText);
            UserMessage userMessage = new UserMessage(requestDto.question());

            Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

            String response = chatModel.call(prompt).getResult().getOutput().getText();

            return ResponseEntity.ok(Map.of("answer", response));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

}
