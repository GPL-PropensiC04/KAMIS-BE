package gpl.karina.profile.restservice;

import java.util.List;
import java.util.UUID;

import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.request.UpdateClientRequestDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;
import gpl.karina.profile.restdto.response.PageResponseDTO;
import gpl.karina.profile.restdto.response.ClientListResponseDTO;

public interface ClientService {
    public ClientResponseDTO addClient(AddClientRequestDTO addClientRequestDTO);
    List<ClientListResponseDTO> getAllClient();
    ClientResponseDTO getClientById(UUID id) throws Exception;
    ClientResponseDTO updateClient(UUID id, UpdateClientRequestDTO updateClientRequestDTO);
    List<ClientListResponseDTO> filterClients(String nameClient, Boolean typeClient, Long minProfit, Long maxProfit);
    PageResponseDTO<ClientListResponseDTO> getAllClientPaginated(int page, int size);
    PageResponseDTO<ClientListResponseDTO> filterClientsPaginated(String nameClient, Boolean typeClient, Long minProfit,
            Long maxProfit, int page, int size);
}
