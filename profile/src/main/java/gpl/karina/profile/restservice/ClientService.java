package gpl.karina.profile.restservice;

import java.util.List;
import java.util.UUID;

import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.request.UpdateClientRequestDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;
import gpl.karina.profile.restdto.response.ClientListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    public ClientResponseDTO addClient(AddClientRequestDTO addClientRequestDTO);
    List<ClientListResponseDTO> getAllClient();
    Page<ClientListResponseDTO> getAllClientPaginated(Pageable pageable); // For paginated list
    ClientResponseDTO getClientById(UUID id) throws Exception;
    ClientResponseDTO updateClient(UUID id, UpdateClientRequestDTO updateClientRequestDTO);
    List<ClientListResponseDTO> filterClients(String nameClient, Boolean typeClient, Long minProfit, Long maxProfit);
    Page<ClientListResponseDTO> filterClientsPaginated(String nameClient, Boolean typeClient, Long minProfit,
            Long maxProfit, Pageable pageable);
}
