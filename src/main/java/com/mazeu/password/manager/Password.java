package com.mazeu.passwordmanager.manager;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.ResourceBundle.getBundle;

@Component
@Scope("prototype")
public class Password implements Comparable<Password> {

    public enum Type { NORMAL, PREFERENTIAL }
    private Type type;
    private Integer passwordNumber; //The number on the current password
    //Every password must be able to access this so that the numbers follow ascending order
    public static Integer lastNumber = Integer.valueOf(
            getBundle("static/password", Locale.ROOT).getString("last_pass"));

    public Password(Type type) {
        this.type = type;
        passwordNumber = lastNumber + 1; //A senha atual é maior que a última
        lastNumber = passwordNumber; //Esta senha agora se torna a mais recente
        PasswordManager.queue.add(this);
    }
    Password(Type type, int number) {
        this.type = type;
        passwordNumber = number;
    }

    //Returna a senha no formato N####
    public String get() {
        StringBuilder finalString = new StringBuilder();
        finalString.append( type == Type.PREFERENTIAL ? "P" : "N" );
        int digitLength = valueOf(passwordNumber).length();
        int paddingSize = 4-digitLength; //A quantidade de zeros a esquerda do numero gerado

        if (paddingSize > 0) {
            for (int i = 0; i < paddingSize; i++) {
                finalString.append("0");
            }
        }
        finalString.append(passwordNumber);
        return finalString.toString();
    }

    //Este comparador coloca as senhas preferenciais na frente da fila
    @Override
    public int compareTo(Password other) {
        if (this.type == other.type)
            return this.passwordNumber.compareTo(other.passwordNumber);

        if (this.type == Type.PREFERENTIAL) return -1;
        else return 1;
    }
}
