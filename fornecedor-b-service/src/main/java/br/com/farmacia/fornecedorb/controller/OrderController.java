package br.com.farmacia.fornecedorb.controller;

import br.com.farmacia.dto.OrderRequest;
import br.com.farmacia.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class OrderController {

    private final Random random = new Random();

    @PostMapping
    public ResponseEntity<OrderResponse> receiveOrder(@RequestBody OrderRequest request) {
        if (random.nextInt(100) < 15) {
            String soapErrorResponse = buildSoapErrorResponse();
            OrderResponse response = parseSoapResponse(soapErrorResponse);
            return ResponseEntity.ok(response);
        }

        String protocol = "FB-" + UUID.randomUUID().toString();
        String soapSuccessResponse = buildSoapSuccessResponse(protocol);

        OrderResponse response = parseSoapResponse(soapSuccessResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Fornecedor B (SOAP/Delphi) — Operacional");
    }


    //helpers
    private String buildSoapSuccessResponse(String protocol) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <EnviarPedidoResponse>
                      <Protocolo>%s</Protocolo>
                      <Status>SUCESSO</Status>
                      <Mensagem>Pedido aceito via SOAP</Mensagem>
                    </EnviarPedidoResponse>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(protocol);
    }

    private String buildSoapErrorResponse() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <soap:Fault>
                      <faultcode>Server</faultcode>
                      <faultstring>DELPHI-ERR: Timeout na conexão</faultstring>
                    </soap:Fault>
                  </soap:Body>
                </soap:Envelope>
                """;
    }

    private OrderResponse parseSoapResponse(String soapXml) {
        String faultString = extractXmlValue(soapXml, "faultstring");

        if (!faultString.isEmpty()) {
            return new OrderResponse(null, "ERRO", faultString);
        }

        String protocol = extractXmlValue(soapXml, "Protocolo");
        String message = extractXmlValue(soapXml, "Mensagem");
        String status = extractXmlValue(soapXml, "Status");

        return new OrderResponse(protocol, status.isEmpty() ? "SUCESSO" : status, message);
    }

    private String extractXmlValue(String xml, String tag) {
        String openTag = "<" + tag + ">";
        String closeTag = "</" + tag + ">";
        int start = xml.indexOf(openTag);
        int end = xml.indexOf(closeTag);
        if (start >= 0 && end >= 0) {
            return xml.substring(start + openTag.length(), end).trim();
        }
        return "";
    }
}
