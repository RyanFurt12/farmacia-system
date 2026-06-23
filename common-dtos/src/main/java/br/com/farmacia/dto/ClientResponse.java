package br.com.farmacia.dto;

import java.time.LocalDate;

public class ClientResponse {

    private Long id;
    private String cpf;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private Boolean hasInsurance;
    private LocalDate registrationDate;

    public ClientResponse() {}

    public ClientResponse(Long id, String cpf, String name, String email, String phone,
                          LocalDate birthDate, Boolean hasInsurance, LocalDate registrationDate) {
        this.id = id;
        this.cpf = cpf;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.hasInsurance = hasInsurance;
        this.registrationDate = registrationDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Boolean getHasInsurance() { return hasInsurance; }
    public void setHasInsurance(Boolean hasInsurance) { this.hasInsurance = hasInsurance; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}
