package gpl.karina.profile.restservice;

import java.util.List;
import java.util.Optional;

import gpl.karina.profile.restdto.request.LoginRequestDTO;
import gpl.karina.profile.restdto.request.AddUserReqeuestDTO;
import gpl.karina.profile.restdto.request.UpdateUserReqeuestDTO;
import gpl.karina.profile.restdto.response.EndUserResponseDTO;
import gpl.karina.profile.restdto.response.LoginResponseDTO;

public interface EndUserService {
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    public void addUser(AddUserReqeuestDTO addUserReqeuestDTO);
    public Optional<EndUserResponseDTO> findByEmail(String email);
    public Optional<EndUserResponseDTO> updateUser(String email, UpdateUserReqeuestDTO addUserReqeuestDTO);
    public List<EndUserResponseDTO> getAllUsers();
}
