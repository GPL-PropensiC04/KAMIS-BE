package gpl.karina.project.restcontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonSerializable.Base;

import gpl.karina.project.restservice.ProjectService;
import jakarta.validation.Valid;
import gpl.karina.project.restdto.request.AddProjectRequestDTO;
import gpl.karina.project.restdto.request.UpdateProjectPaymentRequestDTO;
import gpl.karina.project.restdto.request.UpdateProjectRequestDTO;
import gpl.karina.project.restdto.response.ActivityLineDTO;
import gpl.karina.project.restdto.response.BaseResponseDTO;
import gpl.karina.project.restdto.response.ProjectResponseWrapperDTO;
import gpl.karina.project.restdto.response.SellDistributionSummaryDTO;
import gpl.karina.project.restdto.response.listProjectResponseDTO;
import gpl.karina.project.restdto.request.UpdateProjectStatusRequestDTO;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<ProjectResponseWrapperDTO>> addProject(
            @Valid @RequestBody AddProjectRequestDTO projectRequestDTO,
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

    @PutMapping("update/{id}")
    public ResponseEntity<BaseResponseDTO<ProjectResponseWrapperDTO>> updateProject(
            @PathVariable(name = "id", required = true) String id,
            @Valid @RequestBody UpdateProjectRequestDTO updateProjectRequestDTO,
            BindingResult bindingResult) throws Exception {
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

        // Ensure IDs match
        if (!id.equals(updateProjectRequestDTO.getId())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("ID dalam path dan body request tidak cocok");
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            ProjectResponseWrapperDTO projectResponseDTO = projectService.updateProject(updateProjectRequestDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil memperbarui proyek");
            response.setTimestamp(new Date());
            response.setData(projectResponseDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Gagal memperbarui proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal memperbarui proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<ProjectResponseWrapperDTO>> getDetailProject(
            @PathVariable(name = "id") String id) {
        var response = new BaseResponseDTO<ProjectResponseWrapperDTO>();
        try {
            ProjectResponseWrapperDTO project = projectService.getProjectById(id);
            if (project == null) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Proyek tidak ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan detail proyek");
            response.setTimestamp(new Date());
            response.setData(project);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal mendapatkan detail proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<listProjectResponseDTO>>> getListProject(
            @RequestParam(name = "idProject", required = false) String idSearch,
            @RequestParam(name = "statusProject", required = false) String projectStatus,
            @RequestParam(name = "tipeProject", required = false) Boolean projectType,
            @RequestParam(name = "namaProject", required = false) String projectName,
            @RequestParam(name = "clientProject", required = false) String projectClientId,
            @RequestParam(name = "tanggalMulai", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date projectStartDate,
            @RequestParam(name = "tanggalSelesai", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date projectEndDate,
            @RequestParam(name = "startNominal", required = false) Long startNominal,
            @RequestParam(name = "endNominal", required = false) Long endNominal) {
        BaseResponseDTO<List<listProjectResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<listProjectResponseDTO> listProject = projectService.getAllProject(idSearch, projectStatus,
                    projectType, projectName, projectClientId, projectStartDate, projectEndDate, startNominal,
                    endNominal);
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

    @PutMapping("/update-status/{id}")
    public ResponseEntity<BaseResponseDTO<ProjectResponseWrapperDTO>> updateProjectStatus(
            @PathVariable(name = "id") String id,
            @RequestBody UpdateProjectStatusRequestDTO updateProjectStatusDTO) {
        BaseResponseDTO<ProjectResponseWrapperDTO> response = new BaseResponseDTO<>();
        try {
            ProjectResponseWrapperDTO updatedProject = projectService.updateProjectStatus(id,
                    updateProjectStatusDTO.getProjectStatus());
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Status proyek berhasil diperbarui");
            response.setTimestamp(new Date());
            response.setData(updatedProject);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Untuk error validasi, gunakan BAD_REQUEST
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Gagal memperbarui status proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Untuk error lain (misal project benar-benar tidak ditemukan)
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Gagal memperbarui status proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-payment/{id}")
    public ResponseEntity<BaseResponseDTO<ProjectResponseWrapperDTO>> updateProjectPayment(
            @PathVariable(name = "id") String id,
            @RequestBody UpdateProjectPaymentRequestDTO updateProjectPaymentDTO) {
        BaseResponseDTO<ProjectResponseWrapperDTO> response = new BaseResponseDTO<>();
        try {
            ProjectResponseWrapperDTO updatedProject = projectService.updateProjectPayment(id,
                    updateProjectPaymentDTO.getProjectPaymentStatus());
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Status proyek berhasil diperbarui");
            response.setTimestamp(new Date());
            response.setData(updatedProject);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Untuk error validasi, gunakan BAD_REQUEST
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Gagal memperbarui status proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Untuk error lain (misal project benar-benar tidak ditemukan)
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Gagal memperbarui status proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/chart/penjualan-activity")
    public ResponseEntity<BaseResponseDTO<List<ActivityLineDTO>>> getPenjualanActivityLineChart(
        @RequestParam(name = "periodType", required = false) String periodType,
        @RequestParam(name = "range", defaultValue = "THIS_YEAR") String range,
        @RequestParam(name = "status", defaultValue = "ALL") String status
    ) {
        BaseResponseDTO<List<ActivityLineDTO>> response = new BaseResponseDTO<>();
        try {
            List<ActivityLineDTO> data = projectService.getProjectActivityLine(periodType, range, status, false); // false = penjualan

            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada data aktivitas penjualan yang ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan data aktivitas penjualan per periode");
            response.setTimestamp(new Date());
            response.setData(data);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Parameter tidak valid: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil data aktivitas penjualan: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/chart/distribusi-activity")
    public ResponseEntity<BaseResponseDTO<List<ActivityLineDTO>>> getDistribusiActivityLineChart(
        @RequestParam(name = "periodType", required = false) String periodType,
        @RequestParam(name = "range", defaultValue = "THIS_YEAR") String range,
        @RequestParam(name = "status", defaultValue = "ALL") String status
    ) {
        BaseResponseDTO<List<ActivityLineDTO>> response = new BaseResponseDTO<>();
        try {
            List<ActivityLineDTO> data = projectService.getProjectActivityLine(periodType, range, status, true); // true = distribusi

            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada data aktivitas distribusi yang ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan data aktivitas distribusi per periode");
            response.setTimestamp(new Date());
            response.setData(data);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Parameter tidak valid: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil data aktivitas distribusi: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<BaseResponseDTO<SellDistributionSummaryDTO>> getSellDistributionSummary(
            @RequestParam(name = "range", defaultValue = "THIS_YEAR") String rangeParam) {

        BaseResponseDTO<SellDistributionSummaryDTO> response = new BaseResponseDTO<>();

        try {
            SellDistributionSummaryDTO result = projectService.getSellDistributionSummaryByRange(rangeParam);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan ringkasan proyek penjualan dan distribusi untuk range: " + rangeParam);
            response.setTimestamp(new Date());
            response.setData(result);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Parameter range tidak valid: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil data ringkasan proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/range")
    public ResponseEntity<BaseResponseDTO<List<listProjectResponseDTO>>> getProjectListByRange(
            @RequestParam(name = "range", defaultValue = "THIS_YEAR") String rangeParam) {

        BaseResponseDTO<List<listProjectResponseDTO>> response = new BaseResponseDTO<>();

        try {
            List<listProjectResponseDTO> result = projectService.getProjectListByRange(rangeParam);

            if (result.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada data proyek untuk range: " + rangeParam);
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan daftar proyek untuk range: " + rangeParam);
            response.setTimestamp(new Date());
            response.setData(result);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Parameter range tidak valid: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil data proyek: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
