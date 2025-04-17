package gpl.karina.profile.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpl.karina.profile.model.Client;
import gpl.karina.profile.repository.ClientRepository;
import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.request.UpdateClientRequestDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;
import gpl.karina.profile.restdto.response.ClientListResponseDTO;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    private ClientResponseDTO clientToClientResponseDTO(Client client) {
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO();
        clientResponseDTO.setId(client.getId());
        clientResponseDTO.setNameClient(client.getNameClient());
        clientResponseDTO.setNoTelpClient(client.getNoTelpClient());
        clientResponseDTO.setEmailClient(client.getEmailClient());
        clientResponseDTO.setCompanyClient(client.getCompanyClient());
        clientResponseDTO.setAddressClient(client.getAddressClient());
        clientResponseDTO.setCreatedDate(client.getCreatedDate());
        clientResponseDTO.setUpdatedDate(client.getUpdatedDate());

        if (client.isTypeClient()) {
            clientResponseDTO.setTypeClient("Perusahaan");
        } else {
            clientResponseDTO.setTypeClient("Perorangan");
        }

        //TODO: attribute untuk hubungin client sama Distribusi & Penjualan (financial history ambil dari sini juga)

        return clientResponseDTO;
    }

    public ClientResponseDTO addClient(AddClientRequestDTO addClientRequestDTO) {
        Client client = new Client();
        client.setNameClient(addClientRequestDTO.getNameClient());
        client.setNoTelpClient(addClientRequestDTO.getNoTelpClient());
        client.setEmailClient(addClientRequestDTO.getEmailClient());
        client.setTypeClient(addClientRequestDTO.isTypeClient());
        client.setCompanyClient(addClientRequestDTO.getCompanyClient());
        client.setAddressClient(addClientRequestDTO.getAddressClient());

        Client newClient = clientRepository.save(client);

        return clientToClientResponseDTO(newClient);
    }

    @Override
    public List<ClientListResponseDTO> getAllClient() {
        List<Client> clients = clientRepository.findAll();
        List<ClientListResponseDTO> clientListResponseDTOs = new ArrayList<>();
        for (Client client : clients) {
            ClientListResponseDTO clientListResponseDTO = listClientToClientResponseDTO(client);
            clientListResponseDTOs.add(clientListResponseDTO);
        }
        return clientListResponseDTOs;
    }

    @Override
    public ClientResponseDTO getClientById(UUID id) {
        return clientRepository.findById(id)
            .map(this::clientToClientResponseDTO)
            .orElse(null);
    }

    @Override
    public ClientResponseDTO updateClient(UUID id, UpdateClientRequestDTO updateClientRequestDTO) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        if (updateClientRequestDTO.getNameClient() != null) {
            client.setNameClient(updateClientRequestDTO.getNameClient());
        }
        if (updateClientRequestDTO.getNoTelpClient() != null) {
            client.setNoTelpClient(updateClientRequestDTO.getNoTelpClient());
        }
        if (updateClientRequestDTO.getEmailClient() != null) {
            client.setEmailClient(updateClientRequestDTO.getEmailClient());
        }
        if (updateClientRequestDTO.getAddressClient() != null) {
            client.setAddressClient(updateClientRequestDTO.getAddressClient());
        }

        Client updatedClient = clientRepository.save(client);
        return clientToClientResponseDTO(updatedClient);
    }

    @Override
    public List<ClientListResponseDTO> filterClients(String nameClient, Boolean typeClient) {
        List<Client> clients;
        
        if (nameClient != null && typeClient != null) {
            clients = clientRepository.findByNameClientContainingIgnoreCaseAndTypeClient(nameClient, typeClient);
        } else if (nameClient != null) {
            clients = clientRepository.findByNameClientContainingIgnoreCase(nameClient);
        } else if (typeClient != null) {
            clients = clientRepository.findByTypeClient(typeClient);
        } else {
            clients = clientRepository.findAll();
        }
        
        return clients.stream()
            .map(this::listClientToClientResponseDTO)
            .toList();
    }

    private ClientListResponseDTO listClientToClientResponseDTO(Client client) {
        ClientListResponseDTO clientListResponseDTO = new ClientListResponseDTO();
        clientListResponseDTO.setNameClient(client.getNameClient());
        clientListResponseDTO.setCompanyClient(client.getCompanyClient());

        if (client.isTypeClient()) {
            clientListResponseDTO.setTypeClient("Perusahaan");
        } else {
            clientListResponseDTO.setTypeClient("Perorangan");
        }

        //TODO: attribute untuk hubungin client sama Distribusi & Penjualan (financial history ambil dari sini juga)

        return clientListResponseDTO;
    }
}
