package gpl.karina.profile.restservice;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import gpl.karina.profile.repository.EndUserRepository;
import gpl.karina.profile.model.EndUser;
import gpl.karina.profile.restdto.request.LoginRequestDTO;
import gpl.karina.profile.restservice.EndUserService;
import gpl.karina.profile.restdto.response.EndUserResponseDTO;
import gpl.karina.profile.restdto.response.LoginResponseDTO;
import gpl.karina.profile.security.service.UserDetailsServiceImpl;
import gpl.karina.profile.restdto.request.AddUserReqeuestDTO;
import org.springframework.security.authentication.BadCredentialsException;
import gpl.karina.profile.security.jwt.JwtUtils;

@Service
public class EndUserServiceImpl implements EndUserService {

    private final EndUserRepository endUserRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;


    public EndUserServiceImpl(EndUserRepository endUserRepository, UserDetailsServiceImpl userDetailsService, JwtUtils jwtUtils) {
        this.endUserRepository = endUserRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }
    
    
    @Override
    public Optional<EndUserResponseDTO> findByEmail(String email) {
        return null;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<EndUser> endUser = endUserRepository.findByEmail(loginRequestDTO.getEmail());
        UserDetails userDetails = userDetailsService.loadUserByUsername(endUser.get().getName());
        if (!userDetails.getPassword().equals(loginRequestDTO.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        String token = jwtUtils.generateJwtToken(userDetails.getUsername());
        loginResponseDTO.setToken(token);
        return loginResponseDTO;
    }

    @Override
    public void addUser(AddUserReqeuestDTO addUserReqeuestDTO) {
        EndUser endUser = new EndUser();
        endUser.setEmail(addUserReqeuestDTO.getEmail());
        endUser.setPassword(addUserReqeuestDTO.getPassword());
        endUser.setName(addUserReqeuestDTO.getName());
        endUserRepository.save(endUser);
    }
}
