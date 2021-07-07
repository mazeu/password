package com.mazeu.passwordmanager;

import com.mazeu.passwordmanager.manager.Password;
import com.mazeu.passwordmanager.manager.PasswordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@CrossOrigin
public class SenhaService {
    @Autowired
    PasswordManager passwordManager;
    //Setting up the login endpoint like this allows for sending the credentials through JSON
    @PostMapping("/login")
    String login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        if (validCredentials(username, password)) {
            SecurityContext sc = SecurityContextHolder.getContext();
            Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
            sc.setAuthentication(auth);
            return "AUTHORIZED";
        }
        else {
            return "NOT VALID";
        }
    }

    //Gera uma senha do tipo normal
    @GetMapping("/cliente/senha/normal")
    String normalPassword() {
        return new Password(Password.Type.NORMAL).get();
    }
    //Gera uma senha do tipo preferencial
    @GetMapping("/cliente/senha/preferencial")
    String preferentialPassword() {
        return new Password(Password.Type.PREFERENTIAL).get();
    }
    //Retorna um stream que é atualizado cada vez que uma senha é chamada
    @GetMapping(path = "/cliente/acompanhamento", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux streamFlux() {
        return passwordManager.getProcessor();
    }
    //Chama uma senha e remove da fila
    @GetMapping("/gerente/senha/proxima")
    String nextPassword() {
        return passwordManager.next();
    }
    //Chama a ultima senha
    @GetMapping("/gerente/senha/rechamado")
    String recallPassword() {
        return passwordManager.recall();
    }
    //Zera a contagem das senhas
    @GetMapping("/gerente/senha/redefinir")
    int reset() {
        passwordManager.reset();
        return 0;
    }

    /*
    Verifica se o usuario e a senha estao de  acordo com o perfil
    */
    boolean validCredentials(String username, String password) {
        return ( username.equals("gerente") && password.equals("gerente") );
    }
}
