package gpl.karina.profile.restservice;

import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;

public interface ClientService {
    public ClientResponseDTO addClient(AddClientRequestDTO addClientRequestDTO);
}
