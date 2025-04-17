package gpl.karina.profile.restservice;

import java.util.List;
import java.util.UUID;

import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.request.UpdateClientRequestDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;

public interface ClientService {
    public ClientResponseDTO addClient(AddClientRequestDTO addClientRequestDTO);
    List<ClientResponseDTO> getAllClient();
    ClientResponseDTO getClientById(UUID id) throws Exception;
    ClientResponseDTO updateClient(UUID id, UpdateClientRequestDTO updateClientRequestDTO);
}
