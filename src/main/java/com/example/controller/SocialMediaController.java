package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
@RequestMapping("/")
public class SocialMediaController {
    private final AccountService accountService;
    private final MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> registerUser(@RequestBody Account account) {
        Optional<Account> existingAccount = accountService.findByUsername(account.getUsername());
        if (existingAccount.isPresent()) 
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        if (account.getUsername().isBlank() || account.getPassword().length() < 4) 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        Account savedAccount = accountService.createAccount(account);
        return ResponseEntity.ok(savedAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> loginUser(@RequestBody Account account) {
        Optional<Account> existingAccount = accountService.authenticate(account.getUsername(), account.getPassword());
        return existingAccount.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message.getMessageText().isBlank() || message.getMessageText().length() > 255) 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        if (!accountService.existsById(message.getPostedBy())) 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        Message savedMessage = messageService.createMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/messages/{message_id}")
    public ResponseEntity<Message> getMessageById(@PathVariable int message_id) {
        Optional<Message> message = messageService.getMessageById(message_id);
        return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
    }

    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<String> deleteMessage(@PathVariable int message_id) {
        int rowsDeleted = messageService.deleteMessageById(message_id);
        if (rowsDeleted > 0) 
            return ResponseEntity.ok("1");
        return ResponseEntity.ok(""); 
    }

    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<Integer> updateMessage(@PathVariable int message_id,
                                                                @RequestBody Message updatedMessage) {
        if (updatedMessage.getMessageText().isBlank() || updatedMessage.getMessageText().length() > 255) 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        int rowsUpdated = messageService.updateMessageText(message_id, updatedMessage.getMessageText());
        return rowsUpdated > 0 ? ResponseEntity.ok(rowsUpdated)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable int account_id) {
        return ResponseEntity.ok(messageService.getMessagesByUser(account_id));
    }


}
