package gpl.karina.profile.restcontroller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.request.UpdateClientRequestDTO;
import gpl.karina.profile.restdto.response.BaseResponseDTO;
import gpl.karina.profile.restdto.response.ClientListResponseDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;
import gpl.karina.profile.restservice.ClientService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/client")
public class ClientRestController {
    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<ClientResponseDTO>> addPurchase(
            @Valid @RequestBody AddClientRequestDTO addClientRequestDTO, BindingResult bindingResult) {
        BaseResponseDTO<ClientResponseDTO> response = new BaseResponseDTO<>();
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages.toString());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            response.setStatus(200);
            response.setMessage("Success");
            response.setData(clientService.addClient(addClientRequestDTO));
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<ClientListResponseDTO>>> listClient(
            @RequestParam(required = false) String nameClient, @RequestParam(required = false) Boolean typeClient) {
            var baseResponseDTO = new BaseResponseDTO<List<ClientListResponseDTO>>();
        List<ClientListResponseDTO> listClient;
        String message;

        if (nameClient == null && typeClient == null) {
            // If no filters, return all clients
            listClient = clientService.getAllClient();
            message = "List Client berhasil ditemukan";
        } else {
            // If filters present, return filtered clients
            listClient = clientService.filterClients(nameClient, typeClient);
            message = "List Client berhasil difilter";
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listClient);
        baseResponseDTO.setMessage(message);
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<ClientResponseDTO>> getClientDetail(@PathVariable("id") UUID id) {
        var baseResponseDTO = new BaseResponseDTO<ClientResponseDTO>();
        
        try {
            ClientResponseDTO client = clientService.getClientById(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(client);
            baseResponseDTO.setMessage("Detail Client berhasil ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Client dengan ID " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponseDTO<ClientResponseDTO>> updateClient(@PathVariable UUID id, 
            @RequestBody UpdateClientRequestDTO updateClientRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<ClientResponseDTO>();
        try {
            ClientResponseDTO updatedClient = clientService.updateClient(id, updateClientRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(updatedClient);
            baseResponseDTO.setMessage("Client berhasil diperbarui");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Client dengan ID " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }
}
