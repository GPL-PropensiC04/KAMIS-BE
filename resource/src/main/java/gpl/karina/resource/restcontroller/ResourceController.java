package gpl.karina.resource.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gpl.karina.resource.exception.UserNotFound;
import gpl.karina.resource.exception.UserUnauthorized;
import gpl.karina.resource.exception.DataNotFound;
import gpl.karina.resource.restdto.request.AddResourceDTO;
import gpl.karina.resource.restdto.response.ResourceResponseDTO;
import gpl.karina.resource.restdto.response.BaseResponseDTO;
import gpl.karina.resource.restservice.ResourceRestService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;

import gpl.karina.resource.exception.UserUnauthorized;

@RestController
@RequestMapping("api/resource")
public class ResourceController {
    private final ResourceRestService resourceRestService;

    public ResourceController(ResourceRestService resourceRestService) {
        this.resourceRestService = resourceRestService;
    }

    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<ResourceResponseDTO>> addResource(
            @Valid @RequestBody AddResourceDTO addResourceDTO, BindingResult bindingResult) {
        BaseResponseDTO<ResourceResponseDTO> response = new BaseResponseDTO<>();
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
            response.setData(resourceRestService.addResource(addResourceDTO));
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    @GetMapping("/viewall")
    public ResponseEntity<BaseResponseDTO<List<ResourceResponseDTO>>> getAllResources() {
        var baseResponseDTO = new BaseResponseDTO<List<ResourceResponseDTO>>();
        // String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
    
        try {
            // Mengirim token ke service
            List<ResourceResponseDTO> resources = resourceRestService.getAllResources();
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("OK");
            baseResponseDTO.setData(resources);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    
        } catch (UserNotFound e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (UserUnauthorized e) {
            baseResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
