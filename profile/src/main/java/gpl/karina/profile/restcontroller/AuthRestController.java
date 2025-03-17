package gpl.karina.profile.restcontroller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import org.springframework.http.HttpStatus;

import gpl.karina.profile.restdto.request.LoginRequestDTO;
import gpl.karina.profile.restdto.response.LoginResponseDTO;
import gpl.karina.profile.restdto.response.BaseResponseDTO;
import gpl.karina.profile.restservice.EndUserService;
import gpl.karina.profile.restdto.response.EndUserResponseDTO;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/auth")
public class AuthRestController {
    private final EndUserService endUserService;
    private static final Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    public AuthRestController(EndUserService endUserService) {
        this.endUserService = endUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        BaseResponseDTO<LoginResponseDTO> response = new BaseResponseDTO<>();
        logger.info("Login request received for email: {}", loginRequestDTO.getEmail());
        Optional<EndUserResponseDTO> endUserResponseDTO = endUserService.findByEmail(loginRequestDTO.getEmail());
        if (!endUserResponseDTO.isPresent()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("User not found");
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        try {
            LoginResponseDTO loginResponseDTO = endUserService.login(loginRequestDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Login successful");
            response.setData(loginResponseDTO);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);    
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Login failed: " + e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        
    }
}
