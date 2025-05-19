package gpl.karina.profile.restservice;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import gpl.karina.profile.repository.AdminRepository;
import gpl.karina.profile.repository.DireksiRepository;
import gpl.karina.profile.repository.EndUserRepository;
import gpl.karina.profile.repository.FinanceRepository;
import gpl.karina.profile.repository.OperasionalRepository;
import gpl.karina.profile.model.Admin;
import gpl.karina.profile.model.Direksi;
import gpl.karina.profile.model.EndUser;
import gpl.karina.profile.model.Finance;
import gpl.karina.profile.model.Operasional;
import org.springframework.security.crypto.password.PasswordEncoder;
import gpl.karina.profile.restdto.request.LoginRequestDTO;
import gpl.karina.profile.restdto.response.EndUserResponseDTO;
import gpl.karina.profile.restdto.response.LoginResponseDTO;
import gpl.karina.profile.security.service.UserDetailsServiceImpl;
import gpl.karina.profile.restdto.request.AddUserReqeuestDTO;
import org.springframework.security.authentication.BadCredentialsException;
import gpl.karina.profile.security.jwt.JwtUtils;

@Service
public class EndUserServiceImpl implements EndUserService {

    private final EndUserRepository endUserRepository;
    private final AdminRepository adminRepository;
    private final DireksiRepository direksiRepository;
    private final FinanceRepository financeRepository;
    private final OperasionalRepository operasionalRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public EndUserServiceImpl(EndUserRepository endUserRepository, UserDetailsServiceImpl userDetailsService, JwtUtils jwtUtils, AdminRepository adminRepository,
     DireksiRepository direksiRepository, FinanceRepository financeRepository, OperasionalRepository operasionalRepository, PasswordEncoder passwordEncoder) {
        this.endUserRepository = endUserRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.adminRepository = adminRepository;
        this.direksiRepository = direksiRepository;
        this.financeRepository = financeRepository;
        this.operasionalRepository = operasionalRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    private EndUserResponseDTO endUserToEndUserResponseDTO(EndUser endUser) {
        EndUserResponseDTO endUserResponseDTO = new EndUserResponseDTO();
        endUserResponseDTO.setEmail(endUser.getEmail());
        endUserResponseDTO.setUsername(endUser.getUsername());
        return endUserResponseDTO;
    }

    @Override
    public Optional<EndUserResponseDTO> findByEmail(String email) {
        Optional<EndUser> endUser = endUserRepository.findByEmail(email);
        if (endUser.isEmpty()) {
            return null;
        }
        return Optional.of(endUserToEndUserResponseDTO(endUser.get()));
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<EndUser> endUser = endUserRepository.findByEmail(loginRequestDTO.getEmail());
        if (endUser.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(endUser.get().getUsername());
        
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        String token = jwtUtils.generateJwtToken(userDetails.getUsername());
        loginResponseDTO.setToken(token);
        return loginResponseDTO;
    }

    @Override
    public void addUser(AddUserReqeuestDTO addUserReqeuestDTO) {
        if (addUserReqeuestDTO.getPassword() == null) {
            throw new IllegalArgumentException("Password is required");
        }
        if (addUserReqeuestDTO.getEmail() == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (addUserReqeuestDTO.getUsername() == null) {
            throw new IllegalArgumentException("Username is required");
        }
        addUserReqeuestDTO.setPassword(hashPassword(addUserReqeuestDTO.getPassword()));
        switch (addUserReqeuestDTO.getRole()) {
            case "admin":
                addUserAdmin(addUserReqeuestDTO);
                break;
            case "direksi":
                addUserDireksi(addUserReqeuestDTO);
                break;
            case "finance":
                addUserFinance(addUserReqeuestDTO);
                break;
            case "operasional":
                addUserOperasional(addUserReqeuestDTO);
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void addUserAdmin(AddUserReqeuestDTO addUserReqeuestDTO) {
        Admin endUser = new Admin();
        endUser.setEmail(addUserReqeuestDTO.getEmail());
        endUser.setPassword(addUserReqeuestDTO.getPassword());
        endUser.setUsername(addUserReqeuestDTO.getUsername());
        adminRepository.save(endUser);
    }

    private void addUserDireksi(AddUserReqeuestDTO addUserReqeuestDTO) {
        Direksi endUser = new Direksi();
        endUser.setEmail(addUserReqeuestDTO.getEmail());
        endUser.setPassword(addUserReqeuestDTO.getPassword());
        endUser.setUsername(addUserReqeuestDTO.getUsername());
        direksiRepository.save(endUser);
    }

    private void addUserFinance(AddUserReqeuestDTO addUserReqeuestDTO) {
        Finance endUser = new Finance();
        endUser.setEmail(addUserReqeuestDTO.getEmail());
        endUser.setPassword(addUserReqeuestDTO.getPassword());
        endUser.setUsername(addUserReqeuestDTO.getUsername());
        financeRepository.save(endUser);
    }

    private void addUserOperasional(AddUserReqeuestDTO addUserReqeuestDTO) {
        Operasional endUser = new Operasional();
        endUser.setEmail(addUserReqeuestDTO.getEmail());
        endUser.setPassword(addUserReqeuestDTO.getPassword());
        endUser.setUsername(addUserReqeuestDTO.getUsername());
        operasionalRepository.save(endUser);
    }

    @Override
    public List<EndUserResponseDTO> getAllUsers() {
        return endUserRepository.findAll().stream()
            .map(this::endUserToEndUserResponseDTO)
            .collect(Collectors.toList());
    }
}
