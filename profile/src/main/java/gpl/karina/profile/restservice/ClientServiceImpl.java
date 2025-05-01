package gpl.karina.profile.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.profile.model.Client;
import gpl.karina.profile.repository.ClientRepository;
import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.request.UpdateClientRequestDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;
import gpl.karina.profile.restdto.response.ProjectResponseDTO;
import gpl.karina.profile.restdto.response.BaseResponseDTO;
import gpl.karina.profile.restdto.response.ClientListResponseDTO;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    @Value("${profile.app.projectUrl}")
    private String projectUrl;

    private final WebClient webClientProject = WebClient.create();

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    private List<ProjectResponseDTO> fetchProjectsByClientId(UUID clientId) {
        String url = projectUrl + "/project/all?clientProject=" + clientId;
        
        try {
            return webClientProject
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        // Log the 404 but don't treat it as an exception
                        System.out.println("No projects found for client: " + clientId);
                        return Mono.empty();
                    }
                    // For other client errors, we might want to propagate them
                    return Mono.error(new RuntimeException("Client error: " + response.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    // Log server errors
                    System.err.println("Server error when fetching projects: " + response.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + response.statusCode()));
                })
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<ProjectResponseDTO>>>() {})
                .map(response -> {
                    if (response == null || response.getData() == null) {
                        return new ArrayList<ProjectResponseDTO>();
                    }
                    
                    List<ProjectResponseDTO> projects = response.getData();
                    projects.forEach(ProjectResponseDTO::calculateProfit);
                    return projects;
                })
                .onErrorResume(e -> {
                    System.err.println("Error fetching projects for client " + clientId + ": " + e.getMessage());
                    // Return empty list on error
                    return Mono.just(new ArrayList<>());
                })
                .block();
        } catch (Exception e) {
            System.err.println("Exception when fetching projects for client " + clientId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private ClientResponseDTO clientToClientResponseDTO(Client client) {
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO();
        clientResponseDTO.setId(client.getId());
        clientResponseDTO.setNameClient(client.getNameClient());
        clientResponseDTO.setNoTelpClient(client.getNoTelpClient());
        clientResponseDTO.setEmailClient(client.getEmailClient());
        clientResponseDTO.setCompanyClient(client.getCompanyClient());
        clientResponseDTO.setAddressClient(client.getAddressClient());
        clientResponseDTO.setProjects(fetchProjectsByClientId(client.getId()));
        clientResponseDTO.setCreatedDate(client.getCreatedDate());
        clientResponseDTO.setUpdatedDate(client.getUpdatedDate());

        if (client.isTypeClient()) {
            clientResponseDTO.setTypeClient("Perusahaan");
        } else {
            clientResponseDTO.setTypeClient("Perorangan");
        }

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
    public List<ClientListResponseDTO> filterClients(String nameClient, Boolean typeClient, Long minProfit, Long maxProfit) {
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
            .filter(dto -> (minProfit == null || (dto.getTotalProfit() != null && dto.getTotalProfit() >= minProfit)))
            .filter(dto -> (maxProfit == null || (dto.getTotalProfit() != null && dto.getTotalProfit() <= maxProfit)))
            .toList();
    }

    private ClientListResponseDTO listClientToClientResponseDTO(Client client) {
        List<ProjectResponseDTO> projects = fetchProjectsByClientId(client.getId());
        long totalProfit = 0L;
        if (projects != null) {
            for (ProjectResponseDTO p : projects) {
                if (p.getProfit() != null) {
                    totalProfit += p.getProfit();
                }
            }
        }

        ClientListResponseDTO clientListResponseDTO = new ClientListResponseDTO();
        clientListResponseDTO.setId(client.getId());
        clientListResponseDTO.setNameClient(client.getNameClient());
        clientListResponseDTO.setCompanyClient(client.getCompanyClient());
        clientListResponseDTO.setTypeClient(client.isTypeClient() ? "Perusahaan" : "Perorangan");
        clientListResponseDTO.setProjectCount(projects != null ? projects.size() : 0);
        clientListResponseDTO.setTotalProfit(totalProfit);

        return clientListResponseDTO;
    }
}
