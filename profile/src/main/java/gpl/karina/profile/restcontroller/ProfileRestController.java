package gpl.karina.profile.restcontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpl.karina.profile.restservice.EndUserService;
import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import gpl.karina.profile.restdto.request.AddUserReqeuestDTO;
import gpl.karina.profile.restdto.request.UpdateUserReqeuestDTO;
import gpl.karina.profile.restdto.response.BaseResponseDTO;

import gpl.karina.profile.restdto.response.EndUserResponseDTO;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/profile")
public class ProfileRestController {
    private final EndUserService endUserService;

    public ProfileRestController(EndUserService endUserService) {
        this.endUserService = endUserService;
    }

    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<AddUserReqeuestDTO>> addUser(
            @Valid @RequestBody AddUserReqeuestDTO addUserReqeuestDTO, BindingResult bindingResult) {
        BaseResponseDTO<AddUserReqeuestDTO> response = new BaseResponseDTO<>();
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
            endUserService.addUser(addUserReqeuestDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("User added successfully");
            response.setData(addUserReqeuestDTO);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<EndUserResponseDTO>>> getAllUsers() {
        BaseResponseDTO<List<EndUserResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<EndUserResponseDTO> users = endUserService.getAllUsers();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success");
            response.setData(users);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/paginated")
    public ResponseEntity<BaseResponseDTO<Page<EndUserResponseDTO>>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(name = "username", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "userType", required = false) String userType) {
        var baseResponseDTO = new BaseResponseDTO<Page<EndUserResponseDTO>>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EndUserResponseDTO> usersPage;
            String message;

            if (name != null || email != null || userType != null) {
                // If filters present, return filtered paginated users
                usersPage = endUserService.getAllUsersPaginated(pageable, email, name, userType);
                message = "List Users berhasil difilter";
            } else {
                usersPage = endUserService.getAllUsersPaginated(pageable, null, null, null);
                message = "List Users berhasil ditemukan";
            }

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage(message);
            baseResponseDTO.setData(usersPage);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<EndUserResponseDTO>> updateEndUser(
            @PathVariable(name = "id") String id,
            @RequestBody @Valid UpdateUserReqeuestDTO updateUserReqeuestDTO,
            BindingResult bindingResult) {

        BaseResponseDTO<EndUserResponseDTO> response = new BaseResponseDTO<>();

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
            EndUserResponseDTO updatedUser = endUserService.updateUser(id, updateUserReqeuestDTO);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("User updated successfully");
            response.setData(updatedUser);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Check if this is a duplicate email error
            if (e.getMessage().contains("duplicate key") ||
                    e.getMessage().contains("already exists")) {

                response.setStatus(HttpStatus.CONFLICT.value());
                response.setMessage("Email or username already exists");
                response.setTimestamp(new Date());

                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
