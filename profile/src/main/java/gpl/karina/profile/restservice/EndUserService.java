package gpl.karina.profile.restservice;

import java.util.Optional;

import gpl.karina.profile.restdto.request.LoginRequestDTO;
import gpl.karina.profile.restdto.request.AddUserReqeuestDTO;
import gpl.karina.profile.restdto.response.EndUserResponseDTO;
import gpl.karina.profile.restdto.response.LoginResponseDTO;

public interface EndUserService {
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    public void addUser(AddUserReqeuestDTO addUserReqeuestDTO);
    public Optional<EndUserResponseDTO> findByEmail(String email);
}
