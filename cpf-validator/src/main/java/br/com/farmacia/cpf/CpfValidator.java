package br.com.farmacia.cpf;

public class CpfValidator {

    /**
     * Valida um CPF verificando os dígitos verificadores.
     *
     * @param cpf o CPF a ser validado
     * @return true se o CPF é válido, false caso contrário
     */
    public boolean isValid(String cpf) {
        if (cpf == null || cpf.isBlank()) return false;

        String cleanedCpf = unformat(cpf);
        if (
            cleanedCpf.length() != 11 ||
            !cleanedCpf.matches("\\d{11}")
        ) return false;

        int firstDigit = generateDigit(cleanedCpf, 10);
        int secondDigit = generateDigit(cleanedCpf, 11);

        return Character.getNumericValue(cleanedCpf.charAt(9)) == firstDigit
                && Character.getNumericValue(cleanedCpf.charAt(10)) == secondDigit;
    }

    /**
     * Remove a formatação do CPF (XXX.XXX.XXX-XX).
     *
     * @param cpf o CPF formatado
     * @return o CPF contendo apenas dígitos
     */
    public String unformat(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("[.\\-]", "");
    }


    /**
     * Formata um CPF para o padrão (XXX.XXX.XXX-XX).
     *
     * @param cpf o CPF a ser formatado (apenas dígitos)
     * @return o CPF formatado
     * @throws IllegalArgumentException se o CPF não contém 11 dígitos
     */
    public String format(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("CPF não pode ser nulo");
        }

        String cleaned = unformat(cpf);
        if (cleaned.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos, encontrado: " + cleaned.length());
        }

        return cleaned.substring(0, 3) + "." +
               cleaned.substring(3, 6) + "." +
               cleaned.substring(6, 9) + "-" +
               cleaned.substring(9, 11);
    }

    /**
     * Gera o dígito verificador do CPF.
     *
     * @param cpf digitos parciais do CPF
     * @return o dígito verificador do CPF
     */
    private int generateDigit(String cpf, int len) {
        int sum = 0;
        for (int i = 0; i < len - 1; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (len - i);
        }
        int digit = sum % 11;
        return digit < 2 ? 0 : 11 - digit;
    }
}