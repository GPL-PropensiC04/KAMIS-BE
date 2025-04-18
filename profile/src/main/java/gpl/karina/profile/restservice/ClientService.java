package gpl.karina.profile.restservice;

import java.util.List;
import java.util.UUID;

import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.request.UpdateClientRequestDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;
import gpl.karina.profile.restdto.response.ClientListResponseDTO;

public interface ClientService {
    public ClientResponseDTO addClient(AddClientRequestDTO addClientRequestDTO);
    List<ClientListResponseDTO> getAllClient();
    ClientResponseDTO getClientById(UUID id) throws Exception;
    ClientResponseDTO updateClient(UUID id, UpdateClientRequestDTO updateClientRequestDTO);
    List<ClientListResponseDTO> filterClients(String nameClient, Boolean typeClient);
}
