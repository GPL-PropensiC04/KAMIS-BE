package gpl.karina.profile.restdto.response;

import lombok.Data;

@Data
public class ClientListResponseDTO {
    private String nameClient;
    private String typeClient;
    private String companyClient;

    //TODO: attribute untuk hubungin client sama Distribusi & Penjualan (financial history ambil dari sini juga)
}
