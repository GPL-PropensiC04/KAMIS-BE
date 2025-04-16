package gpl.karina.profile.restservice;

import org.springframework.stereotype.Service;

import gpl.karina.profile.model.Client;
import gpl.karina.profile.repository.ClientRepository;
import gpl.karina.profile.restdto.request.AddClientRequestDTO;
import gpl.karina.profile.restdto.response.ClientResponseDTO;
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
}
