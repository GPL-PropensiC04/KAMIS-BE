package gpl.karina.project.restcontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gpl.karina.project.restservice.ProjectService;
import jakarta.validation.Valid;
import gpl.karina.project.restdto.request.ProjectRequestDTO;
import gpl.karina.project.restdto.response.BaseResponseDTO;
import gpl.karina.project.restdto.response.ProjectResponseWrapperDTO;
import gpl.karina.project.restdto.response.listProjectResponseDTO;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<ProjectResponseWrapperDTO>> addProject(
            @Valid @RequestBody ProjectRequestDTO projectRequestDTO,
            BindingResult bindingResult) throws Exception {
        // @Valid annotation will validate the request body and if there are any errors,
        // it will be stored in bindingResult
        BaseResponseDTO<ProjectResponseWrapperDTO> response = new BaseResponseDTO<>();
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errorMessage.append(fieldError.getDefaultMessage()).append("; ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessage.toString());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            ProjectResponseWrapperDTO projectResponseDTO = projectService.addProject(projectRequestDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil menambahkan proyek baru");
            response.setTimestamp(new Date());
            response.setData(projectResponseDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Gagal menambahkan proyek baru: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal menambahkan proyek baru: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<listProjectResponseDTO>>> getListProject(
            @RequestParam(name = "idProject", required = false) String idSearch,
            @RequestParam(name = "statusProject", required = false) String projectStatus,
            @RequestParam(name = "tipeProject", required = false) String projectType,
            @RequestParam(name = "namaProject", required = false) String projectName,
            @RequestParam(name = "clientProject", required = false) String projectClientId,
            @RequestParam(name = "tanggalMulai", required = false) Date projectStartDate,
            @RequestParam(name = "tanggalSelesai", required = false) Date projectEndDate) {
        BaseResponseDTO<List<listProjectResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<listProjectResponseDTO> listProject = projectService.getAllProject(idSearch, projectStatus,
                    projectType, projectName, projectClientId, projectStartDate, projectEndDate);
            if (listProject.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada proyek yang ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan daftar proyek");
            response.setTimestamp(new Date());
            response.setData(listProject);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal mendapatkan daftar proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
