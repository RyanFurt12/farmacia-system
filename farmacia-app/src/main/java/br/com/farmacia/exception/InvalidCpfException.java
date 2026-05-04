package br.com.farmacia.exception;

public class InvalidCpfException extends RuntimeException {
    public InvalidCpfException(String cpf) {
        super("CPF inválido: " + cpf);
    }
}
