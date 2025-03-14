package com.aplicaciongimnasio.PuraEsencia.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/mensaje")  // Esto es lo que el cliente enviará
    @SendTo("/topic/respuesta")  // El mensaje se enviará al cliente en este tópico
    public String procesarMensaje(String mensaje) throws Exception {
        // Aquí puedes realizar la lógica que desees, como actualizar la cantidad de clases disponibles
        return "Mensaje recibido: " + mensaje;
    }
}
