package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Dto.MedicalRAGrequest;
import com.carebridge.carebridge.Service.ChatService;
import com.carebridge.carebridge.entity.UserDetails;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/ai/chat")
public class AiChatController {
    @Autowired
    private ChatService chatService;
    @PostMapping("/askPatientRecords")
    public ResponseEntity<Flux<String>> askAboutPatient(@RequestBody MedicalRAGrequest request) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userId = userDetails.getId();
            ObjectId patientId = new ObjectId(request.getPatientId());
            return ResponseEntity.ok(this.chatService.askPatientRecords(request.getQuestion(),userId,patientId));
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PatchMapping("/allow/{patientId}")
    public ResponseEntity<?> allowDoctor(@PathVariable("patientId") String patientId) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userId = userDetails.getId();
            ObjectId doctorId = new ObjectId(patientId);
            Boolean result = this.chatService.allowDoctor(userId,doctorId);
            if(result){
                return ResponseEntity.ok().build();
            }
            return new ResponseEntity<>(Map.of(
                    "message","You are not authorized to ask about this Patient's medical records.")
                    ,HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/askMyRecords")
    public ResponseEntity<Flux<String>> askMyRecords(@RequestBody MedicalRAGrequest request) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userId = userDetails.getId();
            return ResponseEntity.ok(this.chatService.askMyRecords(request.getQuestion(),userId));
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    
}
