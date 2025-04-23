package gpl.karina.profile.restdto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class ClientListResponseDTO {
    private UUID id;
    private String nameClient;
    private String typeClient;
    private String companyClient;
    private Integer projectCount;

    //TODO: attribute untuk hubungin client sama Distribusi & Penjualan (financial history ambil dari sini juga)
}
