package gpl.karina.profile.restdto.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ClientResponseDTO {
    private UUID id;
    private String nameClient;
    private String noTelpClient;
    private String emailClient;
    private String typeClient;
    private String companyClient;
    private String addressClient;

    //TODO: attribute untuk hubungin client sama Distribusi & Penjualan (financial history ambil dari sini juga)
    private List<ProjectResponseDTO> projects;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date updatedDate;
}
