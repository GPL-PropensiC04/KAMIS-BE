package gpl.karina.profile.restcontroller;

import gpl.karina.profile.restdto.request.AddSupplierRequestDTO;
import gpl.karina.profile.restdto.request.UpdateSupplierRequestDTO;
import gpl.karina.profile.restdto.response.BaseResponseDTO;
import gpl.karina.profile.restdto.response.SupplierListResponseDTO;
import gpl.karina.profile.restdto.response.SupplierResponseDTO;
import gpl.karina.profile.restservice.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/supplier")
public class SupplierRestController {

    private final SupplierService supplierService;

    public SupplierRestController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<SupplierResponseDTO>> addSupplier(
            @Valid @RequestBody AddSupplierRequestDTO addSupplierRequestDTO, BindingResult bindingResult) {

        BaseResponseDTO<SupplierResponseDTO> response = new BaseResponseDTO<>();

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
            SupplierResponseDTO data = supplierService.addSupplier(addSupplierRequestDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success");
            response.setData(data);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<SupplierListResponseDTO>>> listSuppliers(
            @RequestParam(name = "nameSupplier", required = false) String nameSupplier,
            @RequestParam(name = "companySupplier", required = false) String companySupplier) {

        BaseResponseDTO<List<SupplierListResponseDTO>> response = new BaseResponseDTO<>();
        List<SupplierListResponseDTO> suppliers = supplierService.filterSuppliers(nameSupplier, companySupplier);
        String message = (nameSupplier == null && companySupplier == null)
                ? "List supplier berhasil ditemukan"
                : "List supplier berhasil difilter";

        response.setStatus(HttpStatus.OK.value());
        response.setData(suppliers);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity<BaseResponseDTO<List<SupplierResponseDTO>>> getAllSuppliers() {
        BaseResponseDTO<List<SupplierResponseDTO>> response = new BaseResponseDTO<>();
        List<SupplierResponseDTO> data = supplierService.getAllSuppliers();

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Seluruh supplier berhasil ditemukan");
        response.setData(data);
        response.setTimestamp(new Date());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponseDTO<SupplierResponseDTO>> updateSupplier(
            @Valid @RequestBody UpdateSupplierRequestDTO dto,
            BindingResult bindingResult) {

        BaseResponseDTO<SupplierResponseDTO> response = new BaseResponseDTO<>();

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages.toString());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            SupplierResponseDTO updatedSupplier = supplierService.updateSupplier(dto);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Data supplier berhasil diperbarui");
            response.setData(updatedSupplier);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    
}
