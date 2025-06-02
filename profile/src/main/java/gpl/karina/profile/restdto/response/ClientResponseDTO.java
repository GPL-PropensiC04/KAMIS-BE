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
    private boolean typeClient;
    private String companyClient;
    private String addressClient;

    private List<ProjectResponseDTO> projects;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date updatedDate;
}
